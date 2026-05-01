package com.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val name : String,
    val email : String,
    val hashPassword : String,
)