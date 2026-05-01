package com.routes

import com.models.CreateUserRequest
import com.models.User
import com.models.UserResponse
import com.repositories.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.h2.command.ddl.CreateUser

fun Route.userRoutes() {
    val service = UserRepository()

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

        val user = service.delete(id)
        if (!user) throw NoSuchElementException("User with id $id not found")
        call.respond(user)
    }
}