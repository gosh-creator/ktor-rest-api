package com.models

import org.jetbrains.exposed.dao.id.IntIdTable

object CityTable : IntIdTable("cities") {
    val name = varchar("name", 100)
    val country = varchar("country", 100)
    val population = integer("population")
}