package com.project.backend.auth

import com.project.backend.auth.AuthService
import com.project.backend.auth.RegisterRequest
import com.project.backend.auth.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(auth: AuthService) {
    route("/api") {
        post("/register") {
            try {
                val body = call.receive<RegisterRequest>()
                val resp = auth.register(body.email, body.password)
                call.respond(HttpStatusCode.Created, resp)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Conflict")
            }
        }

        post("/login") {
            try {
                val body = call.receive<LoginRequest>()
                val resp = auth.login(body.email, body.password)
                call.respond(resp)
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid input")
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Unauthorized, e.message ?: "Unauthorized")
            }
        }
    }
}
