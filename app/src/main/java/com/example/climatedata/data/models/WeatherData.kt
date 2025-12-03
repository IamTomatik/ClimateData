package com.example.climatedata.data.models

data class WeatherData(
    val wdID: Int = 0,
    val locID: Int,
    val date: Long,
    // --- Данные с API погоды ---
    val temperature: Double,      // Температура
    val humidity: Double,         // Влажность воздуха
    val precipitation: Double,    // Осадки (мм)
    val windSpeed: Double,        // Скорость ветра
    val pressure: Double,         // Атмосферное давление

    // Boolean - логический тип (true/false)
    // Это прогноз или реальные данные?
    val isForecast: Boolean = false,

    // Источник данных
    val source: String = "api" // api, manual
)
