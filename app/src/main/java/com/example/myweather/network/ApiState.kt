package com.example.myweather.network

import com.example.myweather.model.ResponseModel
import retrofit2.Response

sealed class ApiState{
    class Success (val data: Response<ResponseModel>):ApiState()
    class Failure(val msg: Throwable):ApiState()
    object Loading:ApiState()
}