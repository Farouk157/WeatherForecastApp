package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherForecastResponse
import java.io.IOException

class WeatherRemoteDataSourceImp(private val weatherApiService: WeatherApiService) : WeatherRemoteDataSource {

    override suspend fun getWeatherFromNetwork(location: String, unit:String, language: String): WeatherForecastResponse {
        return try {
            val response = weatherApiService.getWeatherForecastByLocation(location, unit, language)

            if (response.isSuccessful) {
                response.body() ?: throw IOException("Error: Empty Response")
            } else {
                throw IOException("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("Network call failed: ${e.message}")
        }
    }

    override suspend fun getWeatherUsingGPS(
        lat: Double,
        long: Double,
        unit: String,
        language: String
    ): WeatherForecastResponse {
        return try {
            val response = weatherApiService.getWeatherForecastByGPS(lat,long,unit, language)

            if (response.isSuccessful) {
                response.body() ?: throw IOException("Error: Empty Response")
            } else {
                throw IOException("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("Network call failed: ${e.message}")
        }
    }

    override suspend fun getWeatherForecastForSavedLocation(
        lat: Double,
        long: Double,
        unit: String,
        language: String
    ): WeatherForecastResponse {
        return try {
            val response = weatherApiService.getWeatherForecastByGPS(lat,long,unit, language)

            if (response.isSuccessful) {
                response.body() ?: throw IOException("Error: Empty Response")
            } else {
                throw IOException("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("Network call failed: ${e.message}")
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: WeatherRemoteDataSourceImp?= null

        fun getInstance(weatherApiService: WeatherApiService): WeatherRemoteDataSource{
            return INSTANCE?: synchronized(this){
                INSTANCE ?:WeatherRemoteDataSourceImp(weatherApiService).also {
                    INSTANCE = it
                }
            }
        }

    }
}