package com.example.weatherforecast.homefragment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HourlyWeather(
    val time: String, // Time formatted as HH:mm
    val temperature: String, // Temperature formatted as a String
    val weatherIconUrl: String // URL for the weather icon
) : Parcelable