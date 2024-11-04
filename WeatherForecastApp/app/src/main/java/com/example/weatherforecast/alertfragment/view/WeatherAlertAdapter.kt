package com.example.weatherforecast.alertfragment.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.database.WeatherAlert
import java.text.SimpleDateFormat
import java.util.*

class WeatherAlertAdapter(private val onDeleteClick: (WeatherAlert) -> Unit) :
    ListAdapter<WeatherAlert, WeatherAlertAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false) // Use custom layout
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert)
    }

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.date_time_alarm)

        fun bind(alert: WeatherAlert) {
            // Format the date and time for display
            val calendar = Calendar.getInstance().apply {
                set(alert.year, alert.month, alert.day, alert.hour, alert.minute)
            }
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            // Display the formatted date and time of the alert
            dateTextView.text = "$formattedDate"
        }
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<WeatherAlert>() {
        override fun areItemsTheSame(oldItem: WeatherAlert, newItem: WeatherAlert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WeatherAlert, newItem: WeatherAlert): Boolean {
            return oldItem == newItem
        }
    }
}
