package com.example.weatherforecast.homefragment.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.SharedPreferencesManager
import com.example.weatherforecast.model.WeatherForecastResponse
import java.text.SimpleDateFormat
import java.util.Locale

class FutureAdapter(private val sharedPreferencesManager: SharedPreferencesManager) :
    ListAdapter<WeatherForecastResponse, FutureAdapter.FutureViewHolder>(FutureDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.future_details_row, parent, false)
        return FutureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FutureViewHolder, position: Int) {
        val futureDetails = getItem(position)
        holder.bind(futureDetails, sharedPreferencesManager)
    }

    class FutureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tv_day)
        private val tvTemperature: TextView = itemView.findViewById(R.id.tv_futureTemp)
        private val tvWeatherDes: TextView = itemView.findViewById(R.id.tv_futureDesc)
        private val tvHumidity: TextView = itemView.findViewById(R.id.tv_Humidity)
        private val tvPressure: TextView = itemView.findViewById(R.id.tv_Pressure)
        private val tvWind: TextView = itemView.findViewById(R.id.tv_wind)
        private val tvClouds: TextView = itemView.findViewById(R.id.tv_Clouds)
        private val tvRain: TextView = itemView.findViewById(R.id.tv_Rain)
        private val ivWeatherIcon: ImageView = itemView.findViewById(R.id.iv_future)

        fun bind(futureWeather: WeatherForecastResponse, sharedPreferencesManager: SharedPreferencesManager) {
            val weatherData = futureWeather.list.firstOrNull()

            // Retrieve preferences for unit and language
            val selectedUnit = sharedPreferencesManager.getSelectedUnit()
            val selectedLanguage = sharedPreferencesManager.getSelectedLanguage()

            // Update the UI with preferences


            if (weatherData != null) {
                tvTime.text = formatDateTimeTo12Hour(weatherData.dt_txt)
                // Display other weather information
                tvWeatherDes.text = weatherData.weather.firstOrNull()?.description ?: ""
                if(selectedLanguage == "English"){
                    tvHumidity.text = "${weatherData.main.humidity}%"
                    tvPressure.text = "${weatherData.main.pressure} Pa"
                    tvRain.text = "${weatherData.rain?.`3h` ?: 0.0} mm"
                    tvClouds.text = "${weatherData.clouds.all}%"
                    if(selectedUnit == "[متر/ثانية] [°س]" || selectedUnit == "Metric [Meter/s] [°C]"){
                        tvTemperature.text="${weatherData.main.temp} °C"
                        tvWind.text = "${weatherData.wind.speed} m/s"
                    }else{
                        tvTemperature.text="${weatherData.main.temp}°K"
                        tvWind.text = "${weatherData.wind.speed} mile/h"
                    }
                }else{
                    tvHumidity.text = "${weatherData.main.humidity}%"
                    tvPressure.text = "${weatherData.main.pressure}ب.ك "
                    tvRain.text = "${weatherData.rain?.`3h` ?: 0.0}مم "
                    tvClouds.text = "${weatherData.clouds.all}%"
                    if(selectedUnit == "[متر/ثانية] [°س]" || selectedUnit == "Metric [Meter/s] [°C]"){
                        tvTemperature.text="${weatherData.main.temp}س° "
                        tvWind.text = "${weatherData.wind.speed}م/ث "
                    }else{
                        tvTemperature.text="${weatherData.main.temp}ك° "
                        tvWind.text = "${weatherData.wind.speed}ميل/س "
                    }
                }


                // Load weather icon using Glide
                val weatherIconCode = weatherData.weather.firstOrNull()?.icon?.replace("n", "d")
                val weatherIconUrl = "https://openweathermap.org/img/wn/$weatherIconCode@2x.png"
                Glide.with(itemView.context)
                    .load(weatherIconUrl)
                    .into(ivWeatherIcon)
            }
        }

        private fun formatDateTimeTo12Hour(dateTime: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            return date?.let { outputFormat.format(it) } ?: dateTime
        }
    }

    class FutureDiffCallback : DiffUtil.ItemCallback<WeatherForecastResponse>() {
        override fun areItemsTheSame(oldItem: WeatherForecastResponse, newItem: WeatherForecastResponse): Boolean {
            return oldItem.city.name == newItem.city.name
        }

        override fun areContentsTheSame(oldItem: WeatherForecastResponse, newItem: WeatherForecastResponse): Boolean {
            return oldItem == newItem
        }
    }
}
