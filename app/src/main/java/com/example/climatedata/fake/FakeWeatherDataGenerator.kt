package com.example.climatedata.data.fake

import com.example.climatedata.data.models.Planting
import com.example.climatedata.data.models.WeatherData
import kotlin.random.Random

object FakeWeatherDataGenerator {

    /**
     * Генерация псевдоданных погоды для указанной посадки
     */
    fun generateFakeWeatherData(plant: Planting, days: Int = 30): List<WeatherData> {
        val now = System.currentTimeMillis()
        val oneDay = 1000L * 60 * 60 * 24
        val random = Random(System.currentTimeMillis())

        return (0 until days).map { i ->
            WeatherData(
                locID = plant.locID,
                date = now - (days - i) * oneDay,
                temperature = 15 + random.nextDouble() * 15,  // 15–30°C
                humidity = 40 + random.nextDouble() * 50,     // 40–90%
                precipitation = random.nextDouble() * 10,     // 0–10 мм
                windSpeed = random.nextDouble() * 8,
                pressure = 990 + random.nextDouble() * 30
            )
        }
    }

    fun generateFakeWeatherDataForDay(plant: Planting, hoursInterval: Int = 3): List<WeatherData> {
        val now = System.currentTimeMillis()
        val millisInHour = 1000L * 60 * 60
        val random = Random(System.currentTimeMillis())
        val points = 24 / hoursInterval

        return (0 until points).map { i ->
            WeatherData(
                locID = plant.locID,
                date = now - ((points - i) * hoursInterval * millisInHour),
                temperature = 15 + random.nextDouble() * 15,  // 15–30°C
                humidity = 40 + random.nextDouble() * 50,     // 40–90%
                precipitation = random.nextDouble() * 10,
                windSpeed = random.nextDouble() * 8,
                pressure = 990 + random.nextDouble() * 30
            )
        }
    }
}
