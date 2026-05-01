package com.routes

import com.models.CreateUserRequest
import com.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val service : UserService by inject()

    get("/users") {
        call.respond(service.findAll())
    }

    get("/users/{id}") {
        val id = call.parameters["id"] ?.toIntOrNull()
            ?: throw IllegalArgumentException("ID must be a number")

        val user = service.findById(id)
            ?: throw NoSuchElementException("User with id $id not found")

        call.respond(user)
    }

    post("/users") {
        val user = call.receive<CreateUserRequest>()
        val created = service.create(user)
        call.respond(HttpStatusCode.Created, created)
    }

    delete("/users/{id}") {
        val id = call.parameters["id"] ?.toIntOrNull()
            ?: throw IllegalArgumentException("ID must be a number")

        val deleted = service.delete(id)
        if (!deleted) throw NoSuchElementException("User with id $id not found")
        call.respond(HttpStatusCode.NoContent)
    }
}