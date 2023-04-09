package com.example.myweather.network

import com.example.myweather.model.ResponseModel

class FakeRemoteSource(val response: ResponseModel) : RemoteSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        appid: String
    ): ResponseModel {
        return response
    }
}