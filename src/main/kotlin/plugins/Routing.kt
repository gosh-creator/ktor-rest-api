package com.plugins

import com.routes.authRoutes
import com.routes.cityRoutes
import com.routes.userRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK)
        }

        authRoutes()

        authenticate("auth-jwt") {
            cityRoutes()
            userRoutes()
        }
    }
}