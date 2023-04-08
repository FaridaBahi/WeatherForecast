package com.example.myweather.database.roomState

import com.example.myweather.model.ResponseModel
import com.example.myweather.network.ApiState
import retrofit2.Response

sealed class HomeRoomState {
    class Success (val data: Response<ResponseModel>): HomeRoomState()
    class Failure(val msg: Throwable): HomeRoomState()
    object Loading: HomeRoomState()
}