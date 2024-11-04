package com.example.weatherforecast.database

import androidx.lifecycle.LiveData
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse)
    fun getFavoritePlaces():  Flow<List<WeatherForecastResponse>>
    suspend fun removeFavoritePlace(cityName: String)


    fun getAllActiveAlerts(): LiveData<List<WeatherAlert>>

    suspend fun insertAlert(alert: WeatherAlert): Long

    suspend fun updateAlert(alert: WeatherAlert)

    suspend fun deleteAlert(alert: WeatherAlert)
}