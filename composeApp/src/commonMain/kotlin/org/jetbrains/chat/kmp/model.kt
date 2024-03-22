package org.jetbrains.chat.kmp

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.chat.kmp.api.ApiClient

class AppModel(private val database: DatabaseQueries) : ScreenModel {
    private val apiClient = ApiClient()

    private val userId = MutableStateFlow<Long?>(null)

    init {
        val dbUser = database.getUser().executeAsOneOrNull()
        if (dbUser != null) {
            userId.value = dbUser
        }
    }

    private val chatHistory = MutableStateFlow(emptyList<ChatKMPMessage>())

    private val lastAssistantMessage = MutableStateFlow<ChatKMPMessage?>(null)

    private val _inputDisabled = MutableStateFlow(false)

    val authenticated = userId.mapState { it != null }

    val chatName = userId.mapState { it.toString() }

    val systemMessageInputEnabled = MutableStateFlow(false)

    val systemMessage = MutableStateFlow<ChatKMPMessage?>(null)

    val chatMessages = combine(
        chatHistory,
        lastAssistantMessage,
        systemMessageInputEnabled,
        systemMessage,
    ) { history, last, sysEnabled, sysMessage ->
        when {
            sysEnabled && sysMessage != null -> listOf(sysMessage)
            sysEnabled -> emptyList()
            last != null -> history + last
            else -> history
        }
    }.stateIn(emptyList())

    val inputDisabled = _inputDisabled.asStateFlow()

    init {
        screenModelScope.launch {
            launch {
                apiClient.startSession()
            }

            userId.collect { id ->
                when (id) {
                    null -> {
                        chatHistory.value = emptyList()
                        lastAssistantMessage.value = null
                        _inputDisabled.value = false

                        database.logout()
                    }

                    else -> {
                        database.logout()
                        database.login(id)

                        launch {
                            withLockedInput {
                                chatHistory.value = apiClient.loadHistory(id).sortedBy { it.timestamp }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun login(id: Long): Boolean = withLockedInput {
        apiClient.login(id).also {
            if (it) {
                userId.value = id
            }
        }
    } ?: false

    fun logout() {
        userId.value = null
    }

    suspend fun register() = withLockedInput {
        apiClient.register().also { id -> userId.value = id }
    }

    suspend fun sendMessage(text: String): Boolean {
        withLockedInput {
            val userId = userId.value ?: return false

            val sender = if (systemMessageInputEnabled.value) {
                Sender.System
            } else {
                Sender.User
            }

            val newMessage = ChatKMPMessage(
                userId = userId,
                text = text,
                timestamp = Clock.System.now(),
                sender = sender,
            )

            if (systemMessageInputEnabled.value) {
                systemMessage.value = newMessage
            } else {
                chatHistory.value += newMessage
            }

            apiClient.sendMessage(newMessage).collect { chunk ->
                val last = lastAssistantMessage.value
                when {
                    last == null -> {
                        lastAssistantMessage.value = ChatKMPMessage(
                            userId = userId,
                            sender = Sender.Assistant,
                            text = chunk.text,
                            timestamp = chunk.timestamp,
                        )
                    }

                    else -> {
                        val updated = last.copy(
                            text = last.text + chunk.text,
                            timestamp = chunk.timestamp,
                        )

                        if (chunk.isLast) {
                            chatHistory.value += updated
                            lastAssistantMessage.value = null
                        } else {
                            lastAssistantMessage.value = updated
                        }
                    }
                }
            }
        }

        return true
    }

    private fun <T, R> StateFlow<T>.mapState(mappingFunction: (T) -> R): StateFlow<R> =
        map(mappingFunction).stateIn(mappingFunction(value))

    private fun <T> Flow<T>.stateIn(initialValue: T): StateFlow<T> =
        stateIn(screenModelScope, SharingStarted.Lazily, initialValue)

    private suspend inline fun <T> withLockedInput(body: () -> T): T? {
        _inputDisabled.await(false)

        _inputDisabled.value = true
        return body().also {
            _inputDisabled.value = false
        }
    }

    private suspend fun <T> StateFlow<T>.await(value: T) = filter { it == value }.first()

    override fun onDispose() {
        apiClient.close()
    }
}
