package com.example.weatherforecast.model

data class WeatherForecastResponse(
    val city: City,
    val list: List<WeatherData>
)

data class City(
    val name: String,
    val country: String,
    val coord: Coordinates,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class WeatherData(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherDetails>,
    val clouds: Clouds,
    val wind: Wind,
    val dt_txt: String
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class MainWeather(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int
)

data class WeatherDetails(
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(val all: Int)

data class Wind(val speed: Double)
