package com.example.weatherforecast.homefragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.SharedPreferencesManager
import com.example.weatherforecast.databinding.FragmentNextFiveDaysBinding
import com.example.weatherforecast.homefragment.viewmodel.HomeFragmentViewModel
import com.example.weatherforecast.model.WeatherForecastResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NextFiveDaysFragment : Fragment() {

    private lateinit var binding: FragmentNextFiveDaysBinding
    private lateinit var weatherViewModel: HomeFragmentViewModel
    private lateinit var forecastAdapter: FutureAdapter
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var selectedLanguage : String
    private lateinit var selectedUnit : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNextFiveDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        selectedLanguage = sharedPreferencesManager.getSelectedLanguage().let { language ->
            if (language == "Arabic") "ar" else "en"
        }

        selectedUnit = sharedPreferencesManager.getSelectedUnit()

        // Initialize RecyclerView
        forecastAdapter = FutureAdapter(sharedPreferencesManager)
        binding.rvFutureweather.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Retrieve forecast data passed from HomeFragment
        val forecastData = arguments?.getParcelable<WeatherForecastResponse>("forecastData")

        // Ensure forecastData is not null before proceeding
        if (forecastData != null) {
            // Set up the adapter with the retrieved data
            forecastData.list.let { weatherList ->
                // Get today's date in the format "yyyy-MM-dd"
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

                // Create a distinct list of dates starting from the next day
                val distinctDays = mutableMapOf<String, WeatherForecastResponse>()

                // Populate the map with the first occurrence of each day starting from tomorrow
                for (weatherData in weatherList) {
                    val date = weatherData.dt_txt.split(" ")[0] // Extract date only

                    // Only consider dates after today
                    if (date > today && !distinctDays.containsKey(date)) {
                        distinctDays[date] = WeatherForecastResponse(
                            city = forecastData.city,
                            list = listOf(weatherData) // Only keep the current WeatherData item for the adapter
                        )
                    }

                    // Stop adding days if we have 4 distinct future days
                    if (distinctDays.size >= 4) {
                        break
                    }
                }

                // Convert the distinct days map to a list
                val futureDetailsList = distinctDays.values.take(4)
                forecastAdapter.submitList(futureDetailsList)
            }
        } else {
            // Handle the case where forecastData is null
            Toast.makeText(context, "No forecast data available", Toast.LENGTH_SHORT).show()
        }

        // Set back button functionality
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}



