package com.example.myweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myweather.databinding.ActivityMainBinding
import com.example.myweather.home.view.Home
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navController = findNavController(R.id.fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home, R.id.favourites, R.id.alert, R.id.settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}