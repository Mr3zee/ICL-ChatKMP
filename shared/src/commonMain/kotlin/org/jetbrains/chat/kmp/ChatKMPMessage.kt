package org.jetbrains.chat.kmp

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatKMPMessage(
    val userId: Long,
    val sender: Sender,
    val text: String,
    val timestamp: Instant,
)

@Serializable
data class ChatKMPMessageChunk(
    val text: String,
    val timestamp: Instant,
    val isLast: Boolean,
)
