package com.example.climatedata.data.models

data class User(
    val userID: Int = 0,
    var email: String,
    var password: String,
    var name: String,
    val role: String = "user",
    var city: String? = null,
    var photoUri: String? = null
)
