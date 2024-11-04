package com.example.weatherforecast.alertfragment.view
import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.alertfragment.alertreceiver.WeatherAlertReceiver
import com.example.weatherforecast.alertfragment.alertservice.WeatherAlertService
import com.example.weatherforecast.alertfragment.viewmodel.WeatherAlertViewModel
import com.example.weatherforecast.database.LocalDataSourceImpl
import com.example.weatherforecast.database.WeatherAlert
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherApiService
import com.example.weatherforecast.network.WeatherRemoteDataSourceImp
import java.util.Calendar

class WeatherAlertFragment : Fragment() {

    private lateinit var viewModel: WeatherAlertViewModel
    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WeatherAlertAdapter

    // Variables to store selected date and time
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvActiveAlerts.layoutManager = LinearLayoutManager(requireContext())

        viewModel = WeatherAlertViewModel(
            WeatherRepository(
                WeatherRemoteDataSourceImp.getInstance(
                    RetrofitHelper.getInstance().create(WeatherApiService::class.java)
                ),
                LocalDataSourceImpl.getInstance(requireContext())
            )
        )

        adapter = WeatherAlertAdapter { alert ->
            viewModel.deleteAlert(alert)
        }

        binding.rvActiveAlerts.adapter = adapter

        // Observe active alerts
        viewModel.activeAlert.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
        }

        // Set up swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alert = adapter.currentList[position]
                viewModel.deleteAlert(alert)
            }
        })

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.rvActiveAlerts)

        createNotificationChannel()

        binding.ivNewAlert.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            com.example.weatherforecast.R.style.DialogTheme,
            { _, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month
                selectedDay = dayOfMonth
                Log.d("WeatherAlertFragment", "Selected Date: $selectedDay/${selectedMonth + 1}/$selectedYear")
                openTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun openTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            com.example.weatherforecast.R.style.DialogTheme,
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                Log.d("WeatherAlertFragment", "Selected Time: $selectedHour:$selectedMinute")
                handleSelectedDateTime()
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true // Use 24-hour format
        )
        timePickerDialog.show()
    }

    private fun handleSelectedDateTime() {
        val calendar = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
        }
        val alertTime = calendar.timeInMillis
        val currentTime = System.currentTimeMillis()

        // Calculate the delay in milliseconds
        val delay = alertTime - currentTime
        if (delay > 0) {
            // Save the alert to the database
            val alert = WeatherAlert(
                year = selectedYear,
                month = selectedMonth,
                day = selectedDay,
                hour = selectedHour,
                minute = selectedMinute,
                isActive = true
            )

            // Observe the LiveData for the inserted ID
            viewModel.insertAlert(alert).observe(viewLifecycleOwner) { insertedId ->
                if (insertedId != null && insertedId > 0) {
                    // Start the weather alert service with the newly assigned ID
                    startWeatherAlertService(alertTime, insertedId.toInt())
                    Log.d("WeatherAlertFragment", "Alert created and saved with ID: $insertedId")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Selected time must be in the future", Toast.LENGTH_SHORT).show()
        }
    }


    // Start the service to set the alarm
    private fun startWeatherAlertService(alertTime: Long, id: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), WeatherAlertReceiver::class.java).apply {
            putExtra("ALERT_TYPE", "NOTIFICATION") // Pass other necessary data
            putExtra("ALERT_ID", id.toString())
        }

        // Updated the PendingIntent to include FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm to trigger at the specified time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alert Channel"
            val descriptionText = "Channel for weather alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("weather_alert_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (requireContext().checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
