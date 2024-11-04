package com.example.weatherforecast.database

import androidx.room.TypeConverter
import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.WeatherData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCity(city: City?): String? {
        return city?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toCity(cityString: String?): City? {
        return cityString?.let { gson.fromJson(it, City::class.java) }
    }

    @TypeConverter
    fun fromWeatherDataList(list: List<WeatherData>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWeatherDataList(listString: String?): List<WeatherData>? {
        val listType = object : TypeToken<List<WeatherData>>() {}.type
        return listString?.let { gson.fromJson(it, listType) }
    }

}