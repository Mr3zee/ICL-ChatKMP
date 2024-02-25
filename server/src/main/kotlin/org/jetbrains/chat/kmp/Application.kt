package org.jetbrains.chat.kmp

import SERVER_PORT
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.jetbrains.chat.kmp.db.dbModule
import org.jetbrains.chat.kmp.services.servicesModule
import org.koin.fileProperties
import org.koin.ktor.plugin.Koin
import java.lang.IllegalArgumentException

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.Upgrade)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true

        // webpack-dev-server and local development
        val allowedHosts = listOf("localhost:3000", "localhost:8080", "127.0.0.1:8080")
        allowedHosts.forEach { host ->
            allowHost(host, listOf("http", "https", "ws", "wss"))
        }
    }

    install(Koin) {
        fileProperties(fileName = "/local.properties")
        modules(dbModule, servicesModule)
    }

    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("Illegal state: ${cause.message}", status = HttpStatusCode.BadRequest)
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respondText("Illegal argument: ${cause.message}", status = HttpStatusCode.BadRequest)
        }
    }

    routing {
        appRouting()
    }
}
