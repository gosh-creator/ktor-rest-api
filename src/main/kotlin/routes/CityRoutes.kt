package com.routes

import com.models.City
import com.services.CityService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.cityRoutes() {
    val service = CityService()

    get("/cities") {
        call.respond(service.getAll())
    }

    get("/cities/{id}") {
        val id = call.parameters["id"] ?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")

        val city = service.getById(id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "City not found")

        call.respond(city)
    }

    post("/cities") {
        val city = call.receive<City>()
        val created = service.create(city)
        call.respond(HttpStatusCode.Created, created)
    }

    delete("/cities/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")

        val deleted = service.delete(id)
        if (deleted) call.respond(HttpStatusCode.NoContent)
        else call.respond(HttpStatusCode.NotFound, "City not found")
    }
}