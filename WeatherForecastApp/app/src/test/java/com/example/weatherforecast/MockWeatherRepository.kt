package com.example.weatherforecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.database.LocalDataSource
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class MockWeatherRepository(
    private val mockLocalDataSource: MockLocalDataSource,
    private val mockRemoteDataSource: MockWeatherRemoteDataSource
) : WeatherRepository(mockRemoteDataSource, mockLocalDataSource) {

    override fun getFavoritePlaces(): Flow<List<WeatherForecastResponse>> {
        return mockLocalDataSource.getFavoritePlaces()
    }

    override suspend fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse) {
        mockLocalDataSource.saveFavoritePlace(weatherForecastResponse)
    }

    override suspend fun removeFavoritePlace(cityName: String) {
        mockLocalDataSource.removeFavoritePlace(cityName)
    }
}
