package com.example.weatherforecast.homefragment.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.homefragment.model.HourlyWeather

class HourlyAdapter : ListAdapter<HourlyWeather, HourlyAdapter.HourlyViewHolder>(HourlyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_details_row, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyWeather = getItem(position)
        holder.bind(hourlyWeather)
    }

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvHours)
        private val tvTemperature: TextView = itemView.findViewById(R.id.tvTemp_hours)
        private val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        fun bind(hourlyWeather: HourlyWeather) {
            tvTime.text = hourlyWeather.time
            tvTemperature.text = hourlyWeather.temperature

            // Load weather icon using Glide
            Glide.with(itemView.context)
                .load(hourlyWeather.weatherIconUrl)
                .into(ivWeatherIcon)
        }
    }

    class HourlyDiffCallback : DiffUtil.ItemCallback<HourlyWeather>() {
        override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
