package com.routes

import com.models.City
import com.models.CreateCityRequest
import com.services.CityService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.cityRoutes() {
    val service : CityService by inject()

    get("/cities") {
        call.respond(service.getAll())
    }

    get("/cities/{id}") {
        val id = call.parameters["id"] ?.toIntOrNull()
            ?: throw IllegalArgumentException("ID must be a number")

        val city = service.getById(id)
            ?: throw NoSuchElementException("City with id $id not found")

        call.respond(city)
    }

    post("/cities") {
        val city = call.receive<CreateCityRequest>()
        val created = service.create(city)
        call.respond(HttpStatusCode.Created, created)
    }

    delete("/cities/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw IllegalArgumentException("ID must be a number")

        val deleted = service.delete(id)
        if (!deleted) throw NoSuchElementException("City with id $id not found")
        call.respond(HttpStatusCode.NoContent)
    }
}