package com.example.myweather.network

import android.util.Log
import com.example.myweather.model.Current
import com.example.myweather.model.Daily
import com.example.myweather.model.ResponseModel
import retrofit2.http.Query

class WeatherClient : RemoteSource {
    val api: ApiService by lazy {
        RetrofitHelper.retrofit_Instance
    }

    companion object {
        private var instance: RemoteSource? = null
        fun getInstance(): RemoteSource {
            return instance ?: synchronized(this) {
                val inst = WeatherClient()
                instance = inst
                inst
            }
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): ResponseModel {
        Log.i("WeatherClient", "getCurrentWeather: ${api.getWeather(lat, lon, lang, appid, units).body()?.timezone}")
        return api.getWeather(lat, lon, lang, appid, units).body() as ResponseModel
    }
}