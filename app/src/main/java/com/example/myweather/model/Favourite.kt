package com.example.myweather.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favourites")
@Parcelize
data class Favourite(@PrimaryKey val name: String, val longitude: Double, val latitude: Double) : Parcelable{
    //constructor(): this("",0.0,0.0)
}