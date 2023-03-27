package com.example.myweather.network

import com.example.myweather.model.Current
import com.example.myweather.model.Daily
import com.example.myweather.model.ResponseModel
import retrofit2.http.Query

class WeatherClient : RemoteSource {
    val api: ApiService by lazy {
        RetrofitHelper.retrofit_Instance
    }

    companion object{
        private var instance: RemoteSource?= null
        fun getInstance(): RemoteSource{
            return instance ?: synchronized(this){
                val inst= WeatherClient()
                instance= inst
                inst
            }
        }
    }

    override suspend fun getCurrentWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): ResponseModel? {
      return api.getWeather(lat, lon, lang, appid,units).body()
    }

    override suspend fun getHourlyWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): List<Current>? {
        return api.getWeather(lat, lon, lang, appid,units).body()?.hourly
    }

    override suspend fun getDailyWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): List<Daily>? {
        return api.getWeather(lat, lon, lang, appid,units).body()?.daily
    }
}