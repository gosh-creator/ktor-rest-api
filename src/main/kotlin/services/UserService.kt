package com.services

import com.models.CreateUserRequest
import com.models.User
import com.models.UserResponse
import com.repositories.UserRepository
import org.h2.command.ddl.CreateUser
import org.jetbrains.exposed.sql.transactions.transaction

class UserService(private val userRepository: UserRepository = UserRepository()) {

    fun getUsers() : List<User> = userRepository.findAll()

    fun getUserById(id : Int) : User? = userRepository.findById(id)

    fun create(user : CreateUserRequest) : UserResponse = userRepository.create(user)

    fun delete (id : Int) : Boolean = userRepository.delete(id)
}