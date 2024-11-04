package com.example.weatherforecast.alertfragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.launch

class WeatherAlertViewModel(private val repository: WeatherRepository) : ViewModel() {

    val activeAlert: LiveData<List<WeatherAlert>> = repository.getAllActiveAlerts()

    fun insertAlert(alert: WeatherAlert): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            val id = repository.insertAlert(alert)
            result.postValue(id) // Post the assigned ID to the LiveData
        }
        return result
    }

    fun updateAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.updateAlert(alert)
        }
    }

    fun deleteAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }
}
