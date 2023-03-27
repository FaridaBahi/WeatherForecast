package com.example.myweather.network

import com.example.myweather.model.Current
import com.example.myweather.model.Daily
import com.example.myweather.model.ResponseModel

interface RemoteSource {
    suspend fun getCurrentWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): ResponseModel?
     suspend fun getHourlyWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): List<Current>?
     suspend fun getDailyWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): List<Daily>?
}