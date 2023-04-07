package com.example.myweather.database

import androidx.room.TypeConverter
import com.example.myweather.model.Current
import com.example.myweather.model.Daily
import com.example.myweather.model.ResponseModel
import com.example.myweather.model.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromCurrentToString(current: Current):String{
        return  Gson().toJson(current)
    }
    @TypeConverter
    fun fromStringToCurrent(currentString: String): Current {
        return Gson().fromJson(currentString, Current::class.java)
    }
    @TypeConverter
    fun fromDailyToString(daily: Daily): String{
        return Gson().toJson(daily)
    }
    @TypeConverter
    fun fromStringToDaily(dailyString: String): Daily {
        return Gson().fromJson(dailyString, Daily::class.java)
    }
    @TypeConverter
    fun fromWeatherToString(weather:List<Weather>): String{
        return Gson().toJson(weather)
    }
    @TypeConverter
    fun fromStringToWeather(weatherString: String): List<Weather> {
        return Gson().fromJson(weatherString, Array<Weather>::class.java).toList()
    }
    @TypeConverter
    fun fromHourlyToString(hourly: List<Current?>?): String? {
        if (hourly == null) {
            return null
        }
        val gson = Gson()
        val type= object :
            TypeToken<List<Current?>?>() {}.type
        return gson.toJson(hourly, type)
    }

    @TypeConverter
    fun fromStringToHourly(hourlyString: String?): List<Current>? {
        if (hourlyString == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Current?>?>() {}.type
        return gson.fromJson<List<Current>>(hourlyString, type)
    }
    @TypeConverter
    fun fromDailyListToString(dailyList: List<Daily?>?): String? {
        if (dailyList == null) {
            return null
        }
        val gson = Gson()
        val type= object :
            TypeToken<List<Daily?>?>() {}.type
        return gson.toJson(dailyList, type)
    }

    @TypeConverter
    fun fromStringToDailyList(dailyString: String?): List<Daily>? {
        if (dailyString == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Daily?>?>() {}.type
        return gson.fromJson<List<Daily>>(dailyString, type)
    }
}