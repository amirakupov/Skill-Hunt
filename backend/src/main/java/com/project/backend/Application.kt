package com.project.backend
import com.project.backend.auth.*

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import com.project.backend.db.DatabaseFactory

import com.project.backend.auth.AuthService
fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1) initialize your database (Hikari + Exposed)
    DatabaseFactory.init()

    // 2) install JSON
    install(ContentNegotiation) {
        json()
    }

    // 3) wire up your service with the concrete repo implementation
    val authService = AuthService(UserRepositoryImpl)

    // 4) set up routing
    routing {
        get("/health") {
            call.respondText("OK")
        }
        userRoutes(authService)
    }
}

