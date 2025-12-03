package com.example.climatedata.data.models
//(Детекция болезней)
data class DiseaseDetection(
    val ddID: Int = 0,
    val planID: Int,
    val imagePath: String,
    // Название обнаруженной болезни
    val detectedDisease: String,
    // Уверенность AI (от 0 до 1)
    val confidence: Double, // точность AI (0-1)
    val detectionDate: Long,
    val recommendedAction: String
)
