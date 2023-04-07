package com.example.myweather.database

import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun getLocalCurrentWeather() : ResponseModel
    suspend fun deleteCurrentWeather()
    suspend fun insertCurrentWeather(data : ResponseModel)
    fun getAllSavedLocation() : Flow<List<Favourite>>
    suspend fun insertLocation(location : Favourite)
    suspend fun removeSavedLocation(location : Favourite)
}