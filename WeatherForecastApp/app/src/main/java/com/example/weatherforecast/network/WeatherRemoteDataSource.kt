package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherForecastResponse


interface WeatherRemoteDataSource {
    suspend fun getWeatherFromNetwork(location: String): WeatherForecastResponse
}