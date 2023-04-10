package com.example.myweather.model

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "alert", primaryKeys = ["currentTime"])
data class AlertModel(

    var latitude: String? = null,
    var longitude: String? = null,
    var currentTime: String,
    var startDate: String? = null,
    var endDate: String? = null,
    var address: String? = null,
    var alertEnabled : Boolean

) : Serializable {}