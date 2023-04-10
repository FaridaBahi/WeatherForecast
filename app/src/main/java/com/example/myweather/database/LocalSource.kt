package com.example.myweather.database

import com.example.myweather.model.AlertModel
import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    fun getLocalCurrentWeather() : Flow<ResponseModel>
    suspend fun deleteCurrentWeather()
    suspend fun insertCurrentWeather(data : ResponseModel)
    fun getAllSavedLocation() : Flow<List<Favourite>>
    suspend fun insertLocation(location : Favourite)
    suspend fun removeSavedLocation(location : Favourite)
    fun getStoredAlerts(): Flow<List<AlertModel>>

    suspend fun insertAlert(alert: AlertModel) : Long

    suspend fun deleteAlert(alert: AlertModel)
}