package com.example.myweather.database

import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(private val favList: MutableList<Favourite> = mutableListOf()) :
    LocalSource {
    override fun getLocalCurrentWeather(): Flow<ResponseModel> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeather(data: ResponseModel) {
        TODO("Not yet implemented")
    }

    override fun getAllSavedLocation(): Flow<List<Favourite>> {
        return flowOf(favList)
    }

    override suspend fun insertLocation(location: Favourite) {
        favList.add(location)
    }

    override suspend fun removeSavedLocation(location: Favourite) {
        favList.remove(location)
    }
}