package com.example.weatherforecast.homefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeFragmentViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherForecastResponse?>()
    val weatherData: LiveData<WeatherForecastResponse?> get() = _weatherData

    fun getWeatherData(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.fetchWeatherByLocation(location)
                // Use postValue to update LiveData safely from the background thread
                _weatherData.postValue(response)
            } catch (e: Exception) {
                // Optionally log the error or handle it accordingly
                _weatherData.postValue(null) // Optional: Resetting data on error, adjust as needed
            }
        }
    }
}
