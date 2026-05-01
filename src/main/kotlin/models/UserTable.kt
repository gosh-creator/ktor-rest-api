package com.models

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("users") {
    val name = varchar("name", 100)
    val email = varchar("email", 100)
    val hashPassword = varchar("hashPassword", 100)
}