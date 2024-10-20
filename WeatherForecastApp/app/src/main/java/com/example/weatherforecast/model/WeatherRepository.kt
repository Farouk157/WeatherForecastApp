package com.example.weatherforecast.model

import com.example.weatherforecast.network.WeatherRemoteDataSource

class WeatherRepository(private val weatherRemoteDataSource: WeatherRemoteDataSource) {
    suspend fun fetchWeatherByLocation(location:String):WeatherForecastResponse{
        return weatherRemoteDataSource.getWeatherFromNetwork(location)
    }
}