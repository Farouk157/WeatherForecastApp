package com.example.weatherforecast.homefragment.view

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.R
import com.example.weatherforecast.SharedPreferencesManager
import com.example.weatherforecast.database.LocalDataSourceImpl
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.homefragment.model.HourlyWeather
import com.example.weatherforecast.homefragment.viewmodel.HomeFragmentViewModel
import com.example.weatherforecast.homefragment.viewmodel.WeatherViewModelFactory
import com.example.weatherforecast.mapfragment.viewmodel.SharedViewModel
import com.example.weatherforecast.model.City
import com.example.weatherforecast.model.Clouds
import com.example.weatherforecast.model.Coordinates
import com.example.weatherforecast.model.MainWeather
import com.example.weatherforecast.model.WeatherData
import com.example.weatherforecast.model.WeatherDetails
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.model.Wind
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherApiService
import com.example.weatherforecast.network.WeatherRemoteDataSourceImp
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.qamar.curvedbottomnaviagtion.log
import org.intellij.lang.annotations.Language
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var weatherViewModel: HomeFragmentViewModel
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var selectedLanguage: String
    private lateinit var selectedUnit: String
    private lateinit var unit: String
    private lateinit var pa: String
    private lateinit var ra: String
    private lateinit var wind_unit : String
    private lateinit var locationCallback: LocationCallback
    private var fromFavorite : Boolean = false
    private var fromMap: Boolean = false
    private lateinit var high: String
    private lateinit var low: String

    private val calendar by lazy { Calendar.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up status bar transparency
        activity?.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        weatherViewModel = HomeFragmentViewModel(WeatherRepository(
            WeatherRemoteDataSourceImp.getInstance(RetrofitHelper.getInstance().create(WeatherApiService::class.java)),
            LocalDataSourceImpl.getInstance(requireContext())))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        selectedLanguage = sharedPreferencesManager.getSelectedLanguage().let { language ->
            if (language == "Arabic") "ar" else "en"
        }

        selectedUnit = sharedPreferencesManager.getSelectedUnit()

        if(selectedLanguage == "en"){
            high = "H: "
            low = "L: "
            pa = "Pa"
            ra ="mm"
            if(selectedUnit == "Metric [Meter/s] [°C]"){
                selectedUnit = "metric"
                unit = "°C"
                wind_unit = "m/s"
            }else{
                selectedUnit = "imperial"
                unit = "°K"
                wind_unit = "mile/h"
            }
        }else{
            high = "ع: "
            low = "ص: "
            pa = "ب.ك"
            ra = "مم"
            if (selectedUnit == "[متر/ثانية] [°س]" || selectedUnit == "Metric [Meter/s] [°C]") {
                selectedUnit = "metric"
                unit = "س°"
                wind_unit = "م/ث"
            } else {
                selectedUnit = "imperial"
                unit = "ك°"
                wind_unit = "ميل/س"
            }
        }


        // Retrieve latitude and longitude from arguments
        val lat = arguments?.getDouble("latitude")
        val lon = arguments?.getDouble("longitude")
        fromMap = arguments?.getBoolean("fromMap", false) ?: false

        if (lat != null && lon != null) {
            if (fromMap) {
                // Handle navigation from MapFragment (add any specific behavior here if needed)
                fromMap = true
            } else {
                // Mark that we’re coming from the Favorites list
                fromFavorite = true
            }
            // Fetch weather data for the selected favorite city
            weatherViewModel.getWeatherForSavedLocations(lat, lon, selectedUnit, selectedLanguage)
        } else {
            // Use GPS-based location only if no latitude and longitude are provided
            if (checkLocationPermission()) {
            } else {
                getCurrentLocation()
                requestLocationPermission()
            }
        }

        val hourlyAdapter = HourlyAdapter()
        binding.rvHoursDetails.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.ivSaveBack.visibility = View.GONE

        weatherViewModel.weatherData.observe(viewLifecycleOwner) { response ->
            updateUI(response)
            response?.let {

                val sortedHourlyData = it.list.sortedBy { weatherData -> weatherData.dt }

                val currentDate = sortedHourlyData.first().dt_txt.split(" ")[0]

                val desiredHours = listOf(0, 3, 6, 9, 12, 15, 18, 21)

                val hourlyData = sortedHourlyData
                    .filter { weatherData ->
                        // Check if the date matches and the hour is in the desired hours
                        val date = weatherData.dt_txt.split(" ")[0]
                        val hour = formatDateTimeToHour(weatherData.dt_txt)
                        date == currentDate && hour in desiredHours
                    }
                    .map { weatherData ->
                        HourlyWeather(
                            time = formatDateTimeTo12Hour_HDetails(weatherData.dt_txt),
                            temperature = "${weatherData.main.temp}${unit}",
                            weatherIconUrl = "https://openweathermap.org/img/wn/${weatherData.weather[0].icon}@2x.png"
                        )
                    }
                hourlyAdapter.submitList(hourlyData)
            }
        }



        binding.tvFutureWeather.setOnClickListener {
            val forecastData = weatherViewModel.weatherData.value

            if (forecastData != null) {
                val bundle = Bundle().apply {
                    putParcelable("forecastData", forecastData)
                }

                val fragment = NextFiveDaysFragment().apply {
                    arguments = bundle
                }

                // Perform the fragment transaction
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(context, "Forecast data is unavailable", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivInsert.setOnClickListener {
            val currentWeatherData = weatherViewModel.weatherData.value
            if (currentWeatherData != null) {
                var temp : WeatherForecastResponse = currentWeatherData
                temp.cityName = currentWeatherData.city.name
                weatherViewModel.saveFavoritePlace(currentWeatherData)
                Toast.makeText(context, "Location saved to favorites!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No weather data available to save", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivSaveBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun updateUI(response: WeatherForecastResponse?) {
        if (response != null) {
            binding.apply {
                progressBar.visibility = View.GONE
                tvCity.text = "${response.city.name}"
                tvWeatherDes.text = "${response.list[0].weather[0].description}"
                tvTemp.text = "${response.list[0].main.temp}${unit}"
                tvHumidity.text = "${response.list[0].main.humidity}%"
                tvPressure.text = "${response.list[0].main.pressure} ${pa}"
                tvHighLowTemp.text = "${high}${response.list[0].main.temp_max}        ${low}${response.list[0].main.temp_min}"
                tvWind.text = "${response.list[0].wind.speed} ${wind_unit}"
                tvClouds.text = "${response.list[0].clouds.all}%"
                tvDateTime.text = formatDateTimeTo12Hour(response.list[0].dt_txt)
                tvRain.text = "${response.list[0].rain?.`3h` ?: 0.0} ${ra}"
                if(fromFavorite){
                    binding.ivSaveBack.visibility = View.VISIBLE
                    binding.ivInsert.visibility = View.GONE
                }
                else{
                    if(fromMap){
                        binding.ivSaveBack.visibility = View.VISIBLE
                        binding.ivInsert.visibility = View.VISIBLE
                    }else{
                        binding.ivSaveBack.visibility = View.GONE
                        binding.ivInsert.visibility = View.VISIBLE
                    }
                }
                val drawable = if (isNightNow()) {
                    R.drawable.night_bg
                } else {
                    setDynamicallyWallpaper(response.list[0].weather[0].icon)
                }

                ivBackground.setImageResource(drawable)


                setEffectRainSnow(response.list[0].weather[0].icon)
            }
        } else {

            binding.progressBar.visibility = View.VISIBLE
        }
    }


    private fun isNightNow(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }


    private fun setDynamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.sunny_bg
            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else -> 0
        }
    }


    private fun setEffectRainSnow(icon: String) {
        when (icon.dropLast(1)) {
            "01", "02", "03", "04" -> initWeatherView(PrecipType.CLEAR)
            "09", "10", "11" -> initWeatherView(PrecipType.RAIN)
            "13" -> initWeatherView(PrecipType.SNOW)
            "50" -> initWeatherView(PrecipType.CLEAR)
        }
    }

    // Initialize weather view for rain/snow/clear effects
    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }

    // Function to format the time from 24-hour to 12-hour with AM/PM
    private fun formatDateTimeTo12Hour(dateTime: String): String {
        // Input format from the API (24-hour format)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Output format with 12-hour clock and AM/PM
        val outputFormat = SimpleDateFormat("yyyy-MM-dd | hh:mm a", Locale.getDefault())

        // Parse the input date string
        val date = inputFormat.parse(dateTime)

        // Format it into the desired output format
        return date?.let {
            outputFormat.format(it)
        } ?: dateTime // In case of any parsing failure, return the original dateTime string
    }

    fun formatDateTimeTo12Hour_HDetails(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format to show hours and minutes with AM/PM

        return try {
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            dateTime // Return the original string in case of an error
        }
    }

    private fun formatDateTimeToHour(dt_txt: String): Int {
        val time = dt_txt.split(" ")[1].split(":")[0] // Get the hour part from "yyyy-MM-dd HH:mm:ss"
        return time.toInt()
    }



    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            } else {
                Toast.makeText(context, "Location permission is required to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getCurrentLocation() {

        if(!fromFavorite){
            if(!fromMap){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    val locationRequest = LocationRequest.create().apply {
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        interval = 10000
                        fastestInterval = 5000 // Fastest update interval is 5 seconds
                    }
                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {

                            val location: Location? = locationResult.lastLocation
                            if (location != null) {

                                val latitude = location.latitude
                                val longitude = location.longitude



                                Log.i(TAG, "The value of the selectedLanaguage is :=========== ${selectedLanguage} ====================================================================")
                                weatherViewModel.getWeatherDataByGPS(latitude, longitude, selectedUnit, selectedLanguage)
                            } else {
                                // If the location is null, show a toast message
                                Toast.makeText(context, "Unable to get current location", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission()) {
            getCurrentLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

}
