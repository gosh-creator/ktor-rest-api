package com.services

import com.models.CreateUserRequest
import com.models.UserResponse
import com.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun findAll(): List<UserResponse> = userRepository.findAll()
        .map { UserResponse(it.id, it.name, it.email) }

    fun findById(id: Int): UserResponse? = userRepository.findById(id)
        ?.let { UserResponse(it.id, it.name, it.email) }

    fun create(user: CreateUserRequest): UserResponse {
        require(user.name.isNotBlank()) { "Username cannot be blank" }
        require(user.email.isNotBlank()) { "User email cannot be blank" }
        require(user.hashPassword.isNotBlank()) { "Password cannot be blank" }
        return userRepository.create(user)
    }

    fun delete(id: Int): Boolean = userRepository.delete(id)
}