package com.example.weatherforecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Mock class for LocalDataSource
class MockLocalDataSource : LocalDataSource {
    private val favoritePlaces = mutableListOf<WeatherForecastResponse>()
    private val activeAlerts = mutableListOf<WeatherAlert>()

    override suspend fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse) {
        favoritePlaces.add(weatherForecastResponse)
    }

    override fun getFavoritePlaces(): Flow<List<WeatherForecastResponse>> = flow {
        emit(favoritePlaces.toList())
    }

    override suspend fun removeFavoritePlace(cityName: String) {
        favoritePlaces.removeIf { it.cityName == cityName }
    }

    override fun getAllActiveAlerts(): LiveData<List<WeatherAlert>> {
        return MutableLiveData(activeAlerts)
    }

    override suspend fun insertAlert(alert: WeatherAlert): Long {
        activeAlerts.add(alert)
        return alert.id
    }

    override suspend fun updateAlert(alert: WeatherAlert) {
        val index = activeAlerts.indexOfFirst { it.id == alert.id }
        if (index != -1) activeAlerts[index] = alert
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
        activeAlerts.remove(alert)
    }
}