package com.repositories

import com.models.CreateUserRequest
import com.models.User
import com.models.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        transaction {
            UserTable.selectAll().map { it.toUser() }
        }
    }

    suspend fun findById(id: Int): User? = withContext(Dispatchers.IO) {
        transaction {
            UserTable.selectAll()
                .where { UserTable.id eq id }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    suspend fun create(user: CreateUserRequest): User = withContext(Dispatchers.IO) {
        transaction {
            val id = UserTable.insertAndGetId {
                it[name] = user.name
                it[email] = user.email
                it[hashPassword] = user.hashPassword
            }
            User(id = id.value, name = user.name, email = user.email, hashPassword = user.hashPassword)
        }
    }

    suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        transaction {
            UserTable.deleteWhere { UserTable.id eq id } > 0
        }
    }

    private fun ResultRow.toUser() : User = User(
        id = this[UserTable.id].value,
        name = this[UserTable.name],
        email = this[UserTable.email],
        hashPassword = this[UserTable.hashPassword],
    )
}