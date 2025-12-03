package com.example.climatedata.data.models

data class Location(
    val locID: Int = 0,
    val userID: Int,
    val name: String,
    val region: String,
    val area: Double? = null,
    val soilType: String? = null,
    val description: String? = null,
    val type: String,
    val imageUri: String? = null
)
