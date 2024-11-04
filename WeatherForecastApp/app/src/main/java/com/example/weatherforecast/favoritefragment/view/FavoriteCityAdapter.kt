package com.example.weatherforecast.favoritefragment.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.favoritefragment.viewmodel.FavoriteFragmentViewModel
import com.example.weatherforecast.model.WeatherForecastResponse


// FavoriteCityAdapter.kt
class FavoriteCityAdapter(
    private val viewModel: FavoriteFragmentViewModel,
    private val onItemRemoved: (WeatherForecastResponse) -> Unit, // Callback for item removal
    private val onItemSelected: (Double, Double) -> Unit // New callback for item selection
) : ListAdapter<WeatherForecastResponse, FavoriteCityAdapter.FavoriteCityViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteCityViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteCityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteCityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cityNameTextView: TextView = view.findViewById(R.id.tv_cityName)
        private val temperatureTextView: TextView = view.findViewById(R.id.tv_favtemp)
        private val removeIcon: ImageView = view.findViewById(R.id.iv_remove)

        fun bind(weatherForecastResponse: WeatherForecastResponse) {
            cityNameTextView.text = weatherForecastResponse.city.name
            temperatureTextView.text = "${weatherForecastResponse.list.firstOrNull()?.main?.temp ?: "-"} °C"

            // Set up click listener for the remove icon
            removeIcon.setOnClickListener {
                // Trigger the callback to notify the fragment about the removal
                onItemRemoved(weatherForecastResponse)
            }

            // Set up click listener for the item itself
            itemView.setOnClickListener {
                onItemSelected(weatherForecastResponse.city.coord.lat, weatherForecastResponse.city.coord.lon)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WeatherForecastResponse>() {
        override fun areItemsTheSame(
            oldItem: WeatherForecastResponse,
            newItem: WeatherForecastResponse
        ) = oldItem.city.name == newItem.city.name

        override fun areContentsTheSame(
            oldItem: WeatherForecastResponse,
            newItem: WeatherForecastResponse
        ) = oldItem == newItem
    }
}
