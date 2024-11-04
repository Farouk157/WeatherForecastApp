package com.example.weatherforecast

import androidx.lifecycle.LiveData
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.database.WeatherDao
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockWeatherDao : WeatherDao {
    private val weatherData = mutableListOf<WeatherForecastResponse>()

    override suspend fun insertWeather(weatherForecastResponse: WeatherForecastResponse) {
        weatherData.add(weatherForecastResponse)
    }

    override fun getFavoritePlaces(): Flow<List<WeatherForecastResponse>> {
        return flowOf(weatherData) // Return current list as Flow
    }

    override suspend fun deleteFavoritePlace(cityName: String) {
        weatherData.removeIf { it.cityName == cityName }
    }

    override fun getAllActiveAlerts(): LiveData<List<WeatherAlert>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(alert: WeatherAlert): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(alert: WeatherAlert) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
        TODO("Not yet implemented")
    }

    // Other methods omitted for brevity
}
