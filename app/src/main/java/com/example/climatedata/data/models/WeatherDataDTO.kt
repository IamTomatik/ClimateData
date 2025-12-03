package com.example.climatedata.data.models

data class WeatherDataDTO(
    val main: Main,
    val wind: Wind,
    val rain: Map<String, Double>? = null,
    val snow: Map<String, Double>? = null,
    val dt: Long,
    val weather: List<WeatherDescription>
)

data class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Double
)

data class Wind(val speed: Double)

data class WeatherDescription(
    val main: String,
    val description: String
)
fun WeatherDataDTO.toWeatherData(locID: Int): WeatherData {
    val precipitation = rain?.get("1h") ?: snow?.get("1h") ?: 0.0
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

