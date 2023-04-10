package com.example.myweather.database.roomState

import com.example.myweather.model.AlertModel

sealed class AlertRoomState {
    class Success (val data: List<AlertModel>) : AlertRoomState()
    class Failure (val msg: Throwable): AlertRoomState()
    object Loading: AlertRoomState()
}