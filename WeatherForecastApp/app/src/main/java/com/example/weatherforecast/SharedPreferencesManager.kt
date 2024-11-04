package com.example.weatherforecast

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_UNIT = "unit"
        private const val KEY_NOTIFICATION = "notification"
    }

    // Language preference
    fun getSelectedLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "English") ?: "English"
    }

    fun setSelectedLanguage(language: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_LANGUAGE, language)
            apply()
        }
    }

    // Unit preference
    fun getSelectedUnit(): String {
        return sharedPreferences.getString(KEY_UNIT, "Metric [Meter/s] [°C]") ?: "Metric [Meter/s] [°C]"
    }

    fun setSelectedUnit(unit: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_UNIT, unit)
            apply()
        }
    }

    // Notification state preference
    fun getNotificationState(): String {
        return sharedPreferences.getString(KEY_NOTIFICATION, "ON") ?: "ON"
    }

    fun setNotificationState(notificationState: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_NOTIFICATION, notificationState)
            apply()
        }
    }
}
