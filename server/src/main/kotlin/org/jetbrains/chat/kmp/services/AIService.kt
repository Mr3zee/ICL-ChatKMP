package org.jetbrains.chat.kmp.services

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import org.jetbrains.chat.kmp.ChatKMPMessage
import org.jetbrains.chat.kmp.ChatKMPMessageChunk
import org.jetbrains.chat.kmp.Sender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AIService : KoinComponent {
    private val chatService by inject<ChatService>()

    private val apiToken = this.getKoin().getProperty<String>("openai.api.token")
        ?: error(
            "Expected 'openai.api.token' property to be present. " +
                    "Please, add it to local.properties file in server directory. " +
                    "You get get a new token here https://platform.openai.com/api-keys"
        )

    private val openAi by lazy {
        OpenAI(apiToken, logging = LoggingConfig(logLevel = LogLevel.None))
    }

    suspend fun postMessage(message: ChatKMPMessage): Flow<ChatKMPMessageChunk> {
        if (message.sender != Sender.User) {
            error("Can not post message not from a user")
        }

        chatService.addMessage(message.copy(timestamp = Clock.System.now()))
        val messages = chatService.getUserHistory(message.userId)
            .sortedBy { it.timestamp }
            .map { ChatMessage(it.role, it.text) }
        var responseMessageId: Long? = null

        return openAi.chatCompletions(ChatCompletionRequest(ChatModel, messages, n = 1))
            .mapNotNull { chunk ->
                val choice = chunk.choices.single()
                val content = choice.delta.content ?: ""
                val isLast = choice.finishReason != null

                when {
                    content.isEmpty() && !isLast -> null
                    else -> ChatKMPMessageChunk(content, Clock.System.now(), isLast)
                }
            }.onEach {
                val id = chatService.updateOrCreateMessage(message.userId, responseMessageId, it)
                if (responseMessageId == null) {
                    responseMessageId = id
                }
            }
    }

    companion object {
        private val ChatModel = ModelId("gpt-3.5-turbo")
    }
}

private val ChatKMPMessage.role
    get() = when (sender) {
        Sender.Assistant -> ChatRole.Assistant
        Sender.System -> ChatRole.System
        Sender.User -> ChatRole.User
    }
