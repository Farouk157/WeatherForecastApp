package com.example.weatherforecast.favoritefragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.R
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.database.LocalDataSourceImpl
import com.example.weatherforecast.databinding.FragmentFavoriteBinding
import com.example.weatherforecast.favoritefragment.viewmodel.FavoriteFragmentViewModel
import com.example.weatherforecast.favoritefragment.viewmodel.FavoriteFragmentViewModelFactory
import com.example.weatherforecast.homefragment.view.HomeFragment
import com.example.weatherforecast.mapfragment.view.MapFragment
import com.example.weatherforecast.model.WeatherForecastResponse
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.network.WeatherApiService
import com.example.weatherforecast.network.WeatherRemoteDataSourceImp
import com.google.android.material.snackbar.Snackbar

// FavoriteFragment.kt
class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteFragmentViewModel
    private lateinit var favoriteCityAdapter: FavoriteCityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root // Return the root view of the binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = FavoriteFragmentViewModel(
            WeatherRepository(
                WeatherRemoteDataSourceImp.getInstance(RetrofitHelper.getInstance().create(
                    WeatherApiService::class.java)),
                LocalDataSourceImpl.getInstance(requireContext())
            )
        )

        // Initialize and set up the RecyclerView adapter
        favoriteCityAdapter = FavoriteCityAdapter(viewModel,
            onItemRemoved = { weatherForecastResponse -> removeItemWithUndo(weatherForecastResponse) },
            onItemSelected = { lat, lon -> navigateToHomeWithCoordinates(lat, lon) } // New callback for item selection
        )

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteCityAdapter
        }

        // Observe the favorite cities from the ViewModel and update the adapter
        viewModel.favoritePlaces.asLiveData().observe(viewLifecycleOwner) { favoriteCities ->
            favoriteCityAdapter.submitList(favoriteCities)
        }

        // Set up button click to navigate to the map view fragment
        binding.ivAddnewfavcity.setOnClickListener {
            // Create an instance of MapFragment
            val mapFragment = MapFragment()

            // Navigate to MapFragment using FragmentManager
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, mapFragment) // Use your actual container ID
                .addToBackStack(null) // Add this transaction to the back stack
                .commit()
        }
    }

    private fun removeItemWithUndo(weatherForecastResponse: WeatherForecastResponse) {
        // Remove item from ViewModel
        viewModel.removeFavoritePlace(weatherForecastResponse.city.name)

        // Show Snackbar with Undo option
        Snackbar.make(requireView(), "Removed ${weatherForecastResponse.city.name}", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                // Re-add item if Undo is clicked
                viewModel.saveFavoritePlace(weatherForecastResponse)
            }
            .show()
    }

    private fun navigateToHomeWithCoordinates(lat: Double, lon: Double) {
        val bundle = Bundle().apply {
            putDouble("latitude", lat)
            putDouble("longitude", lon)
        }

        val homeFragment = HomeFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, homeFragment)
            .addToBackStack(null)
            .commit()
    }
}
