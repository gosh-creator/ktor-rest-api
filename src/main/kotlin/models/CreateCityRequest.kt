package com.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateCityRequest(
    val name : String,
    val country : String,
    val population : Int
)