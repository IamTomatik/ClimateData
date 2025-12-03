package com.example.climatedata.data.models

data class Recommendation(
    val recomID: Int = 0,
    val planID: Int,
    val userID: Int,
    val type: String,
    // Текст рекомендации
    val message: String,
    // Приоритет
    val priority: String, // low, medium, high, critical
    val generatedDate: Long,
    val isCompleted: Boolean = false

)
