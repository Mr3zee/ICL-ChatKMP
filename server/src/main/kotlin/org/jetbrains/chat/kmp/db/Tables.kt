package org.jetbrains.chat.kmp.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tables {
    object Users : LongIdTable()

    object Messages : LongIdTable() {
        val user = reference("user", Users.id, onDelete = ReferenceOption.CASCADE)
        val message = text("message")
        val role = enumeration<MessageRole>("role")

        val timestamp = datetime("timestamp")
    }
}

enum class MessageRole {
    User, System, Assistant;
}
