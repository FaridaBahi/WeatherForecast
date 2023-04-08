package com.example.myweather.model

import kotlinx.coroutines.flow.Flow


interface RepositoryInterface {
    fun getCurrentWeather(lat:Double, lon:Double, lang:String, units:String, appid:String): Flow<ResponseModel>
    fun getLocalCurrentWeather() : Flow<ResponseModel>
    suspend fun deleteCurrentWeather()
    suspend fun insertCurrentWeather(data : ResponseModel)
    suspend fun getAllSavedLocation() : Flow<List<Favourite>>
    suspend fun insertLocation(location : Favourite)
    suspend fun removeSavedLocation(location : Favourite)

}