package com.repositories

import com.models.CreateUserRequest
import com.models.User
import com.models.UserResponse
import com.models.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun findAll() : List<User> = transaction {
        UserTable.selectAll().map { it.toUser() }
    }

    fun findById(id : Int) : User? = transaction {
        UserTable.selectAll()
            .where { UserTable.id eq id}
            .map { it.toUser() }
            .singleOrNull()
    }

    fun create(user : CreateUserRequest) : UserResponse = transaction {
        val id = UserTable.insertAndGetId {
            it[name] = user.name
            it[email] = user.email
            it[hashPassword] = user.hashPassword
        }

        UserResponse (
            id = id.value,
            name = user.name,
            email = user.email,
        )
    }

    fun delete(id : Int) : Boolean = transaction {
        UserTable.deleteWhere { UserTable.id eq id } > 0
    }

    private fun ResultRow.toUser() : User = User(
        id = this[UserTable.id].value,
        name = this[UserTable.name],
        email = this[UserTable.email],
        hashPassword = this[UserTable.hashPassword],
    )
}