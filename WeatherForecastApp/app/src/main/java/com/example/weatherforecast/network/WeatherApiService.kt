package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeatherForecastByLocation(
        @Query("q") location: String,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = "2819213f5ea8627d88ce8e81e3d0ee3b"
    ): Response<WeatherForecastResponse>

    @GET("forecast")
    suspend fun getWeatherForecastByGPS(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = "2819213f5ea8627d88ce8e81e3d0ee3b"
    ): Response<WeatherForecastResponse>

    @GET("forecast")
    suspend fun getWeatherForecastForSavedLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = "2819213f5ea8627d88ce8e81e3d0ee3b"
    ): Response<WeatherForecastResponse>

}