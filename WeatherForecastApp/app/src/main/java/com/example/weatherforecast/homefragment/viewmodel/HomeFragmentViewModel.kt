package com.example.weatherforecast.homefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragmentViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherForecastResponse?>()
    val weatherData: LiveData<WeatherForecastResponse?> get() = _weatherData

    fun getWeatherData(location: String, unit: String, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.fetchWeatherByLocation(location, unit, language)
                _weatherData.postValue(response)
            } catch (e: Exception) {
                _weatherData.postValue(null)
            }
        }
    }

    fun getWeatherDataByGPS(lat: Double, long: Double, unit: String, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.fetchWeatherByGPS(lat, long, unit, language)
                _weatherData.postValue(response)
            } catch (e: Exception) {
                _weatherData.postValue(null)
            }
        }
    }

    fun getWeatherForSavedLocations(lat: Double, long: Double, unit: String, language: String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getWeatherForecastForSavedLocation(lat,long,unit,language)
                _weatherData.postValue(response)
            }catch (e:Exception){
                _weatherData.postValue(null)
            }
        }
    }

    fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.saveFavoritePlace(weatherForecastResponse)
            } catch (e: Exception) {

            }
        }
    }
}

