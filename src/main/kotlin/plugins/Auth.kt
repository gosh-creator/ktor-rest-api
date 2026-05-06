package com.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    val secret = environment.config.propertyOrNull("jwt.secret")?.getString() ?: "super-puper-secret-key"
    val issuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "ktor-app"

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.subject != null) JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}