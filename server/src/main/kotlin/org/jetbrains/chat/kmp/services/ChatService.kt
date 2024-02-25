package org.jetbrains.chat.kmp.services

import org.jetbrains.chat.kmp.ChatKMPMessage
import org.jetbrains.chat.kmp.ChatKMPMessageChunk
import org.jetbrains.chat.kmp.Sender
import org.jetbrains.chat.kmp.db.DB
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatService : KoinComponent {
    private val db by inject<DB>()

    suspend fun addMessage(message: ChatKMPMessage): Long {
        return db.addMessage(message)
    }

    suspend fun getUserHistory(userId: Long): List<ChatKMPMessage> {
        return db.getUserHistoryById(userId)
    }

    suspend fun updateOrCreateMessage(userId: Long, messageId: Long?, chunk: ChatKMPMessageChunk): Long {
        return if (messageId == null) {
            addMessage(ChatKMPMessage(userId, Sender.Assistant, chunk.text, chunk.timestamp))
        } else {
            db.appendToMessage(messageId, chunk.text, chunk.timestamp)
            messageId
        }
    }
}
