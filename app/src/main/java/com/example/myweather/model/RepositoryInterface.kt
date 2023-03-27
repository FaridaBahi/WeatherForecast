package com.example.myweather.model

interface RepositoryInterface {
    suspend fun getCurrentWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): ResponseModel?
    suspend fun getHourlyWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): List<Current>?
    suspend fun getDailyWeather(lat:Double, lon:Double, lang:String,appid:String,units:String): List<Daily>?
}