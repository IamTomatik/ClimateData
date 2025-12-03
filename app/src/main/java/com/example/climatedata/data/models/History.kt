package com.example.climatedata.data.models


data class History(
    val histID: Int = 0,
    val userID: Int,
    val planID: Int,    // конкретная посадка (может быть null, если общее действие)
    val locID: Int,     // локация (например, теплица / участок)
    val message: String,       // текст действия ("Полив включен", "Автосвет включен", ...)
    val timestamp: Long,       // время (в миллисекундах)
    val date: String           // удобная дата для фильтрации (например, "2025-11-12")
)