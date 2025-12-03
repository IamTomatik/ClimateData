package com.example.climatedata.data.repository


import com.example.climatedata.data.api.RetrofitClient
import com.example.climatedata.data.mappers.toWeatherData
import com.example.climatedata.data.models.WeatherData
import  com.example.climatedata.data.db.ClimateDatabase
import android.util.Log


class WeatherRepository(private val db: ClimateDatabase) {

    suspend fun fetchAndSaveWeather(lat: Double, lon: Double, locID: Int, apiKey: String) {
        try {
            val dto = RetrofitClient.api.getCurrentWeather(lat, lon, apiKey)
            val weatherData = dto.toWeatherData(locID)
            db.addWeatherData(weatherData)
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error fetching weather", e)

        }
    }

    fun getLatestWeather(locID: Int): WeatherData? {
        return db.getWeatherDataForLocation(locID).firstOrNull()
    }


}