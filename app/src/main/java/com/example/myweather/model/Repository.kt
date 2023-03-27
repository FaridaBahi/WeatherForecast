package com.example.myweather.model

import com.example.myweather.network.RemoteSource

class Repository private constructor(var remoteSource: RemoteSource, /*var localSource: LocalDataSource*/)
    : RepositoryInterface {

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource, /*localSource: LocalDataSource*/): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(remoteSource/*, localSource*/)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        appid: String,
        units: String
    ): ResponseModel? {
        return remoteSource.getCurrentWeather(lat, lon, lang, appid, units)
    }

    override suspend fun getHourlyWeather(
        lat: Double,
        lon: Double,
        lang: String,
        appid: String,
        units: String
    ): List<Current>? {
       return remoteSource.getHourlyWeather(lat, lon, lang, appid, units)
    }

    override suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        lang: String,
        appid: String,
        units: String
    ): List<Daily>? {
       return remoteSource.getDailyWeather(lat, lon, lang, appid, units)
    }
}