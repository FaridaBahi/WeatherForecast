package com.example.myweather.favourites.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.model.RepositoryInterface

class FavPreviewViewModelFactory(
    val repo: RepositoryInterface,
    val context: Context
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavPreviewViewModel::class.java)) {
            FavPreviewViewModel(repo, context) as T
        } else {
            throw java.lang.IllegalArgumentException("Fav Preview View Model class is not found")
        }
    }
}