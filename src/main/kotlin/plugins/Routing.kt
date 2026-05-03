package com.plugins

import com.routes.authRoutes
import com.routes.cityRoutes
import com.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()

        authenticate("auth-jwt") {
            cityRoutes()
            userRoutes()
        }
    }
}