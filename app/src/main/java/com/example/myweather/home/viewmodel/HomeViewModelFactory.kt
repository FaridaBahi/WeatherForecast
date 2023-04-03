package com.example.myweather.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.locations.GpsLocation
import com.example.myweather.locations.MapsFragment
import com.example.myweather.model.RepositoryInterface

class HomeViewModelFactory(
    val repo: RepositoryInterface,
    val context: Context,
    val gps: GpsLocation,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(repo, context, gps) as T
        } else {
            throw java.lang.IllegalArgumentException("Home View Model class is not found")
        }
    }
}