package com.example.weatherforecast.model

import androidx.lifecycle.LiveData
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

open class WeatherRepository(private val weatherRemoteDataSource: WeatherRemoteDataSource, private val localDataSource: LocalDataSource) {

        // Fetch weather from network by location
        suspend fun fetchWeatherByLocation(location: String, unit: String, language: String): WeatherForecastResponse {
            return weatherRemoteDataSource.getWeatherFromNetwork(location, unit, language)
        }

        // Fetch weather from network by GPS coordinates
        suspend fun fetchWeatherByGPS(lat: Double, long: Double, unit: String, language: String): WeatherForecastResponse {
            return weatherRemoteDataSource.getWeatherUsingGPS(lat, long, unit, language)
        }

        suspend fun getWeatherForecastForSavedLocation(lat: Double, long: Double, unit: String, language: String): WeatherForecastResponse{
            return weatherRemoteDataSource.getWeatherForecastForSavedLocation(lat, long, unit, language)
        }

        // Save a favorite place to the local database
        open suspend fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse) {
            localDataSource.saveFavoritePlace(weatherForecastResponse)
        }

        // Get a list of favorite places from the local database
        open fun getFavoritePlaces(): Flow<List<WeatherForecastResponse>> {
            return localDataSource.getFavoritePlaces()
        }

        // Remove a favorite place by city name
        open suspend fun removeFavoritePlace(cityName: String) {
            localDataSource.removeFavoritePlace(cityName)
        }

        fun getAllActiveAlerts(): LiveData<List<WeatherAlert>> {
            return localDataSource.getAllActiveAlerts()
        }

        suspend fun insertAlert(alert: WeatherAlert): Long {
            return localDataSource.insertAlert(alert) // Return the ID received from local data source
        }

        suspend fun updateAlert(alert: WeatherAlert) {
            localDataSource.updateAlert(alert)
        }

        suspend fun deleteAlert(alert: WeatherAlert) {
            localDataSource.deleteAlert(alert)
        }
}