package com.example.myweather.network

import com.example.myweather.model.ResponseModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("lang") lang:String,
        @Query("units") units:String,
        @Query("appid") appid:String
    ) : Response<ResponseModel>
}

object RetrofitHelper{
    const val BASE_URL= "https://api.openweathermap.org/data/2.5/"
    val retrofit_Instance= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiService::class.java)
}