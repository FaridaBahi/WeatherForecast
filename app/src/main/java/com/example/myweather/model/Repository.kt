package com.example.myweather.model

import com.example.myweather.database.LocalSource
import com.example.myweather.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(var remoteSource: RemoteSource, var localSource: LocalSource)
    : RepositoryInterface {

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(remoteSource, localSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): ResponseModel? {
        return remoteSource.getCurrentWeather(lat, lon, lang, appid, units)
    }

    override suspend fun getLocalCurrentWeather(): ResponseModel {
        return localSource.getLocalCurrentWeather()
    }

    override suspend fun deleteCurrentWeather() {
        localSource.deleteCurrentWeather()
    }

    override suspend fun insertCurrentWeather(data: ResponseModel) {
        localSource.insertCurrentWeather(data)
    }

    override suspend fun getAllSavedLocation(): Flow<List<Favourite>> {
        return localSource.getAllSavedLocation()
    }

    override suspend fun insertLocation(location: Favourite) {
        localSource.insertLocation(location)
    }

    override suspend fun removeSavedLocation(location: Favourite) {
        localSource.removeSavedLocation(location)
    }
}