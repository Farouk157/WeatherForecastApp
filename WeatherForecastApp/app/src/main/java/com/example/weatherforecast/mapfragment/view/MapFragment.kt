package com.example.weatherforecast.mapfragment.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.weatherforecast.R
import com.example.weatherforecast.homefragment.view.HomeFragment
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private var selectedLocation: GeoPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = view.findViewById(R.id.map)
        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)

        val startPoint = GeoPoint(20.0, 0.0)
        map.controller.setZoom(2.0)
        map.controller.setCenter(startPoint)

        map.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val point = map.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                addMarkerAtPoint(point)
                navigateToHomeFragment(point)
            }
            true
        }

        val backButton: ImageView = view.findViewById(R.id.iv_mapback)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addMarkerAtPoint(point: GeoPoint) {
        map.overlays.clear()

        val marker = Marker(map)
        marker.position = point
        marker.title = "Selected Location"
        marker.icon = resources.getDrawable(R.drawable.baseline_add_location_24)

        marker.setOnMarkerClickListener { _, _ ->
            selectedLocation = marker.position
            navigateToHomeFragment(selectedLocation)
            true
        }

        map.overlays.add(marker)
        marker.showInfoWindow()
    }

    private fun navigateToHomeFragment(location: GeoPoint?) {
        if (location != null) {
            val bundle = Bundle().apply {
                putDouble("latitude", location.latitude)
                putDouble("longitude", location.longitude)
                putBoolean("fromMap", true) // New flag to indicate navigation from MapFragment
            }

            val homeFragment = HomeFragment()
            homeFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDetach()
    }
}
