package com.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.models.CreateUserRequest
import com.services.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.Date

fun Route.authRoutes() {
    val userService by inject<UserService>()
    val secret = "my-secret-key"
    val issuer = "ktor-app"

    post("/auth/register") {
        val request = call.receive<CreateUserRequest>()
        val user = userService.create(request)
        call.respond(HttpStatusCode.Created, user)
    }

    post("/auth/login") {
        val request = call.receive<CreateUserRequest>()

        val token = JWT.create()
            .withIssuer(issuer)
            .withSubject(request.email)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000))
            .sign(Algorithm.HMAC256(secret))

        call.respond(mapOf("token" to token))
    }
}