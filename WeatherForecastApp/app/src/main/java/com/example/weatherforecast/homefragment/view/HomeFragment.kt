package com.example.weatherforecast.homefragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherApiService
import com.example.weatherforecast.network.WeatherRemoteDataSourceImp
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.homefragment.viewmodel.HomeFragmentViewModel
import com.example.weatherforecast.homefragment.viewmodel.WeatherViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var weatherViewModel: HomeFragmentViewModel
    private lateinit var cityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cityTextView = view.findViewById(R.id.cityTextView)
        temperatureTextView = view.findViewById(R.id.temperatureTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        pressureTextView = view.findViewById(R.id.pressureTextView)

        val weatherApiService = RetrofitHelper.getInstance().create(WeatherApiService::class.java)
        val weatherRemoteDataSource = WeatherRemoteDataSourceImp(weatherApiService)
        val weatherRepository = WeatherRepository(weatherRemoteDataSource)

        // Use WeatherViewModelFactory to create the ViewModel
        val factory = WeatherViewModelFactory(weatherRepository)
        weatherViewModel = ViewModelProvider(this, factory).get(HomeFragmentViewModel::class.java)

        // Fetch weather data
        weatherViewModel.getWeatherData("Giza") // Use a default city or implement GPS fetching

        // Observe LiveData for weather data
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { response ->
            updateUI(response)
        }
    }

    private fun updateUI(response: WeatherForecastResponse?) {
        if (response != null) {
            // Update city and country name
            cityTextView.text = "${response.city.name}, ${response.city.country}"

            // Update temperature
            temperatureTextView.text = "${response.list[0].main.temp}°C"

            // Update humidity
            humidityTextView.text = "Humidity: ${response.list[0].main.humidity}%"

            // Update pressure
            pressureTextView.text = "Pressure: ${response.list[0].main.pressure} hPa"
        } else {
            // Handle the case when response is null (optional)
            cityTextView.text = "Error retrieving data"
            temperatureTextView.text = ""
            humidityTextView.text = ""
            pressureTextView.text = ""
        }
    }
}
