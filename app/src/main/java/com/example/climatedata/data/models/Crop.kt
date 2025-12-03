package com.example.climatedata.data.models


data class Crop(
    val cropID: Int = 0,
    val name: String,
    val category: String,
    val optimalTempMin: Double,
    val optimalTempMax: Double,
    val optimalHumidity: Double,
    val soilHumidity: Double,
    val growthDays: Int,
    val riskFactors: String
)
