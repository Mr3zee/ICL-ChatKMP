package org.jetbrains.chat.kmp

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import org.jetbrains.chat.kmp.services.AIService
import org.jetbrains.chat.kmp.services.ChatService
import org.jetbrains.chat.kmp.services.UserService
import org.koin.ktor.ext.inject

fun Routing.appRouting() {
    val ai by inject<AIService>()
    val chatService by inject<ChatService>()
    val userService by inject<UserService>()

    route("/chat") {
        post("/register") {
            call.respond(userService.createUser())
        }

        get("/login") {
            val userId = call.userId
            when {
                userService.loginUser(userId) -> {
                    call.respond(HttpStatusCode.OK)
                }

                else -> {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }

        get("/history") {
            val history = chatService.getUserHistory(call.userId)
            call.respond(history)
        }

        webSocket {
            try {
                while (true) {
                    val message = receiveDeserialized<ChatKMPMessage>()

                    ai.postMessage(message).collect { chunk -> sendSerialized<ChatKMPMessageChunk>(chunk) }
                }
            } catch (e: ClosedReceiveChannelException) {
                // ignore
            } catch (e : ClosedSendChannelException) {
                // ignore
            }
        }
    }
}

private val ApplicationCall.userIdOrNull
    get() = parameters["userId"]?.toLongOrNull()


private val ApplicationCall.userId
    get() = userIdOrNull ?: error("Expected 'userId' in url parameters as Long")
