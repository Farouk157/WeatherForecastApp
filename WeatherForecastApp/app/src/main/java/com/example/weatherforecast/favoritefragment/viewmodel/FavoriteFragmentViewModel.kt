package com.example.weatherforecast.favoritefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteFragmentViewModel(private val repository: WeatherRepository) : ViewModel() {

    // getting the favorite places as a flow !
    val favoritePlaces: Flow<List<WeatherForecastResponse>> = repository.getFavoritePlaces()

    fun saveFavoritePlace(weatherForecastResponse: WeatherForecastResponse) {
        viewModelScope.launch {
            repository.saveFavoritePlace(weatherForecastResponse)
        }
    }

    fun removeFavoritePlace(cityName: String) {
        viewModelScope.launch {
            repository.removeFavoritePlace(cityName)
        }
    }

}