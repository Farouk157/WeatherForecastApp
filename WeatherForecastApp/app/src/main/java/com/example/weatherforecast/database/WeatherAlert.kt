package com.example.weatherforecast.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alert")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val year: Int,
    val month: Int,
    val day : Int,
    val hour : Int,
    val minute: Int,
    val isActive: Boolean
) {
    // Secondary constructor that accepts only the id
    constructor(id: Long) : this(
        id = id,
        year = 0,
        month = 0,
        day = 0,
        hour = 0,
        minute = 0,
        isActive = false
    )
}

enum class AlarmType {
    NOTIFICATION,
    ALARM_SOUND
}