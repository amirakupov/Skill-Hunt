package com.project.backend
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import com.project.backend.db.DatabaseFactory
import com.project.backend.models.AddCourseRequest
import com.project.backend.models.LoginRequest
import com.project.backend.models.RegisterRequest
import com.project.backend.repo.CourseRepositoryImpl
import com.project.backend.repo.UserRepositoryImpl
import com.project.backend.service.AuthService
import io.ktor.http.HttpStatusCode
import com.project.backend.service.CourseService
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import java.util.Date

object JwtConfig {
    private const val secret    = "very-secret-key"
    private const val issuer    = "ktor"
    private const val audience  = "ktor-users"
    private const val expiryMs  = 36_000_00  // 1h

    private val algorithm = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun makeToken(email: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("email", email)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + expiryMs))
            .sign(algorithm)
    }
}

fun Application.module() {

    DatabaseFactory.init()
    install(ContentNegotiation) { json() }
    val authService = AuthService(UserRepositoryImpl)
    val courseService = CourseService(CourseRepositoryImpl)

    // install JWT auth
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor school example"
            verifier(JwtConfig.verifier)
            validate { cred ->
                cred.payload.getClaim("email").asString()?.let { JWTPrincipal(cred.payload) }
            }
        }
    }

    routing {
        get("/health") { call.respondText("OK") }

        route("/api") {
            post("/register") {
                val (email, pass) = call.receive<RegisterRequest>()
                val resp = authService.register(email, pass)
                call.respond(resp)
            }

            post("/login") {
                val (email, pass) = call.receive<LoginRequest>()
                val loginResp = authService.login(email, pass)
                // issue JWT right here:
                val token = JwtConfig.makeToken(loginResp.email)
                call.respond(LoginResponseWithToken(email = loginResp.email, token = token))
            }
            get("/courses/all") {
                val all = courseService.getAllCourses()
                call.respond(all)
            }

            // any route inside this block requires a valid JWT:
            authenticate("auth-jwt") {
                get("/protected") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userEmail = principal.payload.getClaim("email").asString()
                    call.respondText("Hello, $userEmail — you’re in the protected area!")
                }
                route("/courses") {
                    get {
                        val principal = call.principal<JWTPrincipal>()!!
                        val list = courseService.getCourses(principal)
                        call.respond(list)
                    }
                    post {
                        val principal = call.principal<JWTPrincipal>()!!
                        val req       = call.receive<AddCourseRequest>()
                        val resp      = courseService.addCourse(req, principal)
                        call.respond(HttpStatusCode.Created, resp)
                    }
                }
            }
        }
    }
}
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()    // <-- your Application.module()
    }.start(wait = true)
}

@kotlinx.serialization.Serializable
data class LoginResponseWithToken(val email: String, val token: String)


