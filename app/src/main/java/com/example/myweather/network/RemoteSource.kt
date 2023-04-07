package com.example.myweather.network

import com.example.myweather.model.ResponseModel

interface RemoteSource {
    suspend fun getCurrentWeather(lat:Double, lon:Double, lang:String,units:String,appid:String): ResponseModel?
}