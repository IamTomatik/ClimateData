package com.example.climatedata.data.mappers


import com.example.climatedata.data.models.WeatherData
import com.example.climatedata.data.models.WeatherDataDTO

fun WeatherDataDTO.toWeatherData(locID: Int): WeatherData {

    val precipitation = (rain?.get("1h") ?: 0.0) + (snow?.get("1h") ?: 0.0)

    return WeatherData(
        locID = locID,
        date = dt * 1000,
        temperature = main.temp,
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        precipitation = precipitation,
        isForecast = false,
        source = "api"
    )
}

