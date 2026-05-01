package com.plugins

import com.models.CityTable
import com.models.UserTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:15432/cities"
    val user = System.getenv("DB_USER") ?: "user"
    val password = System.getenv("DB_PASSWORD") ?: "password"
    val driver = if (url.contains("postgresql")) "org.postgresql.Driver" else "org.h2.Driver"

    Database.connect(
        url = url,
        driver = driver,
        user = user,
        password = password
    )

    transaction {
        SchemaUtils.create(CityTable, UserTable)
    }
}