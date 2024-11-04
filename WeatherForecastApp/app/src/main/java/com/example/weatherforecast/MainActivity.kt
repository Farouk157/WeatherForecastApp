package com.example.weatherforecast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weatherforecast.alertfragment.view.WeatherAlertFragment
import com.example.weatherforecast.favoritefragment.view.FavoriteFragment
import com.example.weatherforecast.settingsfragment.view.SettingsFragment
import com.example.weatherforecast.homefragment.view.HomeFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1,"Home", R.drawable.home)

        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2,"Favorite", R.drawable.favorite)

        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3,"Alert", R.drawable.alert)

        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4,"Settings", R.drawable.settings)

        )

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1 -> {
                    replaceFragment(HomeFragment())
                }

                2 -> {
                    replaceFragment(FavoriteFragment())
                }

                3 -> {
                    replaceFragment(WeatherAlertFragment())
                }

                4 -> {
                    replaceFragment(SettingsFragment())
                }
            }
        }

        // default values
        replaceFragment(HomeFragment())
        bottomNavigation.show(1)
    }

    private fun replaceFragment (fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}