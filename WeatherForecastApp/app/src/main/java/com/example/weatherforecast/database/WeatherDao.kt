package com.example.weatherforecast.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherforecast.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherForecastResponse: WeatherForecastResponse)

    @Query("SELECT * FROM weather")
    fun getFavoritePlaces(): Flow<List<WeatherForecastResponse>>

    // Use a query to delete by city name
    @Query("DELETE FROM weather WHERE cityName = :cityName")
    suspend fun deleteFavoritePlace(cityName: String)

    @Query("SELECT * FROM weather_alert WHERE isActive = 1")
    fun getAllActiveAlerts(): LiveData<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: WeatherAlert): Long // Return the assigned ID

    @Update
    suspend fun update(alert: WeatherAlert)

    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)

}