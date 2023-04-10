package com.example.myweather.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository: RepositoryInterface{
    var favData: MutableList<Favourite> = mutableListOf()

    var myResponse = ResponseModel(
        lat = 0.0,
        lon = 0.0,
        current = null,
        daily = listOf(),
        hourly = listOf(),
        timezone = "",
        timezoneOffset = 0L
    )
    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): Flow<ResponseModel> {
        return flowOf(myResponse)
    }

    override fun getLocalCurrentWeather(): Flow<ResponseModel> {
        return flowOf(myResponse)
    }

    override suspend fun deleteCurrentWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeather(data: ResponseModel) {
        myResponse= data
    }

    override suspend fun getAllSavedLocation(): Flow<List<Favourite>> {
        return flowOf(favData)
    }

    override suspend fun insertLocation(location: Favourite) {
        favData.add(location)
    }

    override suspend fun removeSavedLocation(location: Favourite) {
        favData.remove(location)
    }
}