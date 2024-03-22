package org.jetbrains.chat.kmp.db

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.chat.kmp.ChatKMPMessage
import org.jetbrains.chat.kmp.Sender
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

val dbModule = module(createdAtStart = true) {
    single { DB() }
}

class DB {
    init {
        DBConnction.init()
    }

    suspend fun createUser(): Long = tx {
        Tables.Users.insert { } get Tables.Users.id
    }.value

    suspend fun loginUser(userId: Long): Boolean = tx {
        Tables.Users.select(Tables.Users.id)
            .where { Tables.Users.id eq userId }
            .empty()
    }.not()

    suspend fun addMessage(newMessage: ChatKMPMessage): Long {
        return tx {
            Tables.Messages.insert {
                it[user] = newMessage.userId
                it[message] = newMessage.text
                it[role] = newMessage.sender.toRole()
                it[timestamp] = newMessage.timestamp.toLocalDateTime(TimeZone.UTC)
            } get Tables.Messages.id
        }.value
    }

    suspend fun appendToMessage(messageId: Long, suffix: String, lastTimestamp: Instant) {
        tx {
            val initial = Tables.Messages
                .select(Tables.Messages.message)
                .where { Tables.Messages.id eq messageId }
                .single()[Tables.Messages.message]

            Tables.Messages.update(
                where = {
                    Tables.Messages.id eq messageId
                }
            ) {
                it[message] = initial + suffix
                it[timestamp] = lastTimestamp.toLocalDateTime(TimeZone.UTC)
            }
        }
    }

    suspend fun getUserHistoryById(userId: Long): List<ChatKMPMessage> = tx {
        Tables.Messages
            .select(Tables.Messages.message, Tables.Messages.role, Tables.Messages.timestamp)
            .where {
                Tables.Messages.user eq userId
            }
            .map {
                ChatKMPMessage(
                    userId = userId,
                    sender = it[Tables.Messages.role].toSender(),
                    text = it[Tables.Messages.message],
                    timestamp = it[Tables.Messages.timestamp].toInstant(TimeZone.UTC)
                )
            }
    }
}

private fun MessageRole.toSender() = when (this) {
    MessageRole.Assistant -> Sender.Assistant
    MessageRole.System -> Sender.System
    MessageRole.User -> Sender.User
}

private fun Sender.toRole() = when (this) {
    Sender.Assistant -> MessageRole.Assistant
    Sender.System -> MessageRole.System
    Sender.User -> MessageRole.User
}

private suspend fun <T> tx(transactionIsolation: Int? = null, statement: suspend Transaction.() -> T): T {
    return suspendedTransactionAsync(transactionIsolation = transactionIsolation, statement = statement).await()
}

private object DBConnction {
    fun init() {
        val allTables = Tables::class.nestedClasses.mapNotNull {
            it.objectInstance as? Table
        }

        Database.connect("jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME", user = DB_USER, password = DB_PASSWORD)

        transaction {
            SchemaUtils.create(*allTables.toTypedArray(), inBatch = true)
        }
    }

    private const val DB_HOST = "localhost"
    private const val DB_PORT = "5432"
    private const val DB_NAME = "chat_kmp"
    private const val DB_USER = "postgres"
    private const val DB_PASSWORD = "postgres"
}
