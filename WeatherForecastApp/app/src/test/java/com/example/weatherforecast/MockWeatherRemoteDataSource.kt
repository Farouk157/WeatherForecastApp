package com.example.weatherforecast

import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.network.WeatherRemoteDataSource

// Mock class for WeatherRemoteDataSource
class MockWeatherRemoteDataSource : WeatherRemoteDataSource {
    var mockResponse: WeatherForecastResponse? = null

    override suspend fun getWeatherFromNetwork(location: String, unit: String, language: String): WeatherForecastResponse {
        return mockResponse ?: throw Exception("Mock response not set")
    }

    override suspend fun getWeatherUsingGPS(lat: Double, long: Double, unit: String, language: String): WeatherForecastResponse {
        return mockResponse ?: throw Exception("Mock response not set")
    }

    override suspend fun getWeatherForecastForSavedLocation(lat: Double, long: Double, unit: String, language: String): WeatherForecastResponse {
        return mockResponse ?: throw Exception("Mock response not set")
    }
}