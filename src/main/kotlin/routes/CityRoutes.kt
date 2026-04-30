package com.routes

import com.models.City
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.cityRoutes() {
    val cities = mutableListOf<City>(
        City(1, "Moscow", "Russia", 12000000),
        City(2, "Berlin", "Germany", 3700000),
    )

    get("/cities") {
        call.respond(cities)
    }

    get("/cities/{id}") {
        val id = call.parameters["id"] ?.toIntOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")

        val city = cities.find { it.id == id }
            ?: return@get call.respond(HttpStatusCode.NotFound, "City not found")

        call.respond(city)
    }
}