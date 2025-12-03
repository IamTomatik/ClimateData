package com.example.climatedata.data.models

data class Planting(
    val planID: Int = 0,
    val locID: Int,
    val cropID: Int,
    val userID: Int,
    val plantedDate: Long,
    val expectedHarvestDate: Long, // Ожидаемая дата сбора урожая
    val area: Double,
    val status: String = "active",
    val imageUri: String? = null,
    val name: String,
)
