package com.example.myweather.database.roomState

import com.example.myweather.model.Favourite

sealed class FavRoomState {
    class Success (val data: List<Favourite>): FavRoomState()
    class Failure(val msg: Throwable): FavRoomState()
    object Loading: FavRoomState()
}