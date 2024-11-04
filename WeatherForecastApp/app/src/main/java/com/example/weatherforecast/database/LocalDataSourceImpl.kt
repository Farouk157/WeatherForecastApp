package com.example.weatherforecast.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val weatherDao: WeatherDao) : LocalDataSource {
    override suspend fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse) {
        weatherDao.insertWeather(weatherForecastResponse)
    }

    override fun getFavoritePlaces(): Flow<List<WeatherForecastResponse>> {
        return weatherDao.getFavoritePlaces()
    }

    override suspend fun removeFavoritePlace(cityName: String) {
        weatherDao.deleteFavoritePlace(cityName)
    }

    override fun getAllActiveAlerts(): LiveData<List<WeatherAlert>> = weatherDao.getAllActiveAlerts()

    override suspend fun insertAlert(alert: WeatherAlert): Long {
        return weatherDao.insert(alert) // This will return the assigned ID
    }

    override suspend fun updateAlert(alert: WeatherAlert) {
        weatherDao.update(alert)
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
        weatherDao.deleteAlert(alert)
    }

    companion object {
        @Volatile
        private var INSTANCE: LocalDataSourceImpl? = null

        fun getInstance(context: Context): LocalDataSourceImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalDataSourceImpl(
                    AppDatabase.getDatabase(context).weatherDao()
                ).also { INSTANCE = it }
            }
        }
    }
}
