package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherForecastResponse


interface WeatherRemoteDataSource {
    suspend fun getWeatherFromNetwork(location: String, unit: String, language: String): WeatherForecastResponse
    suspend fun getWeatherUsingGPS(lat:Double, long: Double, unit:String, language:String): WeatherForecastResponse
    suspend fun getWeatherForecastForSavedLocation(lat:Double, long: Double, unit:String, language:String): WeatherForecastResponse
}