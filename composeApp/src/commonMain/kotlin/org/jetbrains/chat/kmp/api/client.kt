package org.jetbrains.chat.kmp.api

import SERVER_PORT
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.jetbrains.chat.kmp.ChatKMPMessage
import org.jetbrains.chat.kmp.ChatKMPMessageChunk

private val ktorClient by lazy {
    HttpClient {
        defaultRequest {
            url {
                host = serverHost
                port = SERVER_PORT
            }
        }

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        install(ContentNegotiation) {
            json()
        }
    }
}

expect val serverHost: String

class ApiClient {
    private val sendChannel = Channel<ChatKMPMessage>(Channel.BUFFERED)
    private val receiveChannel = Channel<ChatKMPMessageChunk>(Channel.BUFFERED)

    suspend fun startSession() {
        ktorClient.webSocket("/chat") {
            try {
                while (true) {
                    val message = sendChannel.receive()
                    sendSerialized(message)
                    while (true) {
                        val chunk = receiveDeserialized<ChatKMPMessageChunk>()
                        receiveChannel.send(chunk)
                        if (chunk.isLast) {
                            break
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                // ignore
            } catch (e : ClosedSendChannelException) {
                // ignore
            }
        }
    }

    suspend fun loadHistory(userId: Long): List<ChatKMPMessage> {
        val response = ktorClient.get("/chat/history") {
            parameter("userId", userId)
        }

        return response.body()
    }

    suspend fun sendMessage(message: ChatKMPMessage): Flow<ChatKMPMessageChunk> {
        sendChannel.send(message)
        return flow {
            for (chunk in receiveChannel) {
                emit(chunk)

                if (chunk.isLast) {
                    break
                }
            }
        }
    }

    suspend fun register(): Long {
        return ktorClient.post("/chat/register").body()
    }

    suspend fun login(userId: Long): Boolean {
        return ktorClient.get("/chat/login") {
            parameter("userId", userId)
        }.status == HttpStatusCode.OK
    }
    
    fun close() {
        sendChannel.close()
        receiveChannel.close()
    }
}
