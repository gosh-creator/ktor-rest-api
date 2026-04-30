package com.plugins

import com.routes.cityRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        cityRoutes()
    }
}