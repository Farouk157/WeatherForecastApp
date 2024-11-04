package com.example.weatherforecast.alertfragment.alertservice

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.example.weatherforecast.alertfragment.alertreceiver.WeatherAlertReceiver

class WeatherAlertService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alertTime = intent?.getLongExtra("ALERT_TIME", 0L) ?: 0L
        val alertType = intent?.getStringExtra("ALERT_TYPE") ?: "NOTIFICATION"
        val alertId = intent?.getStringExtra("ALERT_ID") ?: "-1"

        // Set the alarm for the specified alert time
        val alarmIntent = Intent(this, WeatherAlertReceiver::class.java).apply {
            putExtra("ALERT_TYPE", alertType)
            putExtra("ALERT_ID", alertId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alertId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)

        // Stop the service after setting the alarm
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}