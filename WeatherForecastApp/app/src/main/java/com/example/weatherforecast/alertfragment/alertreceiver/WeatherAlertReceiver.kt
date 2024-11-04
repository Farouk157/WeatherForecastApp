package com.example.weatherforecast.alertfragment.alertreceiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.R
import com.example.weatherforecast.alertfragment.viewmodel.WeatherAlertViewModel
import com.example.weatherforecast.database.LocalDataSourceImpl
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherApiService
import com.example.weatherforecast.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertType = intent.getStringExtra("ALERT_TYPE")
        val alertId = intent.getStringExtra("ALERT_ID")?.toLongOrNull() ?: -1L // Use toLongOrNull for safety
        when (alertType) {
            "NOTIFICATION" -> {
                showNotification(context)

                if (alertId != -1L) {
                    deleteAlert(context, alertId)
                }
            }
            "ALARM_SOUND" -> playAlarmSound(context)
        }
    }

    private fun showNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, "weather_alert_channel")
            .setSmallIcon(R.drawable.notifications_alert)
            .setContentTitle("Weather Alert")
            .setContentText("A weather alert has been triggered!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    }

    private fun playAlarmSound(context: Context) {
        // Check if the permission to vibrate is granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val ringtone = RingtoneManager.getRingtone(context, alarmUri)
                ringtone.play()
            } catch (e: SecurityException) {
                Log.e("WeatherAlertReceiver", "SecurityException: ${e.message}")
            }
        } else {
            Log.e("WeatherAlertReceiver", "VIBRATE permission not granted.")
        }
    }

    private fun deleteAlert(context: Context, alertId: Long) {
        Log.d("WeatherAlertReceiver", "Attempting to delete alert with ID: $alertId")
        val viewModel = WeatherAlertViewModel(WeatherRepository(
            WeatherRemoteDataSourceImp.getInstance(
                RetrofitHelper.getInstance().create(WeatherApiService::class.java)
            ),
            LocalDataSourceImpl.getInstance(context)
        ))


        val alertToDelete = WeatherAlert(id = alertId)

        // Launch a coroutine to delete the alert
        CoroutineScope(Dispatchers.IO).launch {
            try {
                viewModel.deleteAlert(alertToDelete)
                Log.d("WeatherAlertReceiver", "Alert with ID: $alertId deleted.")
            } catch (e: Exception) {
                Log.e("WeatherAlertReceiver", "Failed to delete alert with ID: $alertId - ${e.message}")
            }
        }
    }
}
