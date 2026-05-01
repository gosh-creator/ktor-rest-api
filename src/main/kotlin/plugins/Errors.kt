package com.plugins

import com.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureErrors() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,
                ErrorResponse(400, cause.message ?: "Bad Request"))
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound,
                ErrorResponse(404, cause.message ?: "Not Found"))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError,
                ErrorResponse(500, cause.message ?: "Internal Server Error"))
        }
    }
}