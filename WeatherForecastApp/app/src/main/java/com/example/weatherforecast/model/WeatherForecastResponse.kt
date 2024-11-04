package com.example.weatherforecast.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "weather")
data class WeatherForecastResponse(
    val city: City,
    val list: List<WeatherData>,
    @PrimaryKey var cityName: String = city.name
) : Parcelable

@Parcelize
data class City(
    val name: String,
    val country: String,
    val coord: Coordinates,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
) : Parcelable

@Parcelize
data class WeatherData(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherDetails>,
    val clouds: Clouds,
    val wind: Wind,
    val rain: Rain?, // Add this for rain data, nullable because it may not always be present
    val dt_txt: String
) : Parcelable

@Parcelize
data class Coordinates(
    val lat: Double,
    val lon: Double
) : Parcelable

@Parcelize
data class MainWeather(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int
) : Parcelable

@Parcelize
data class WeatherDetails(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) : Parcelable

@Parcelize
data class Clouds(val all: Int) : Parcelable

@Parcelize
data class Wind(val speed: Double) : Parcelable

@Parcelize
data class Rain(
    val `3h`: Double? // Rain in the last 3 hours (nullable)
) : Parcelable