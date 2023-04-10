package com.example.myweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.model.AlertModel
import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel


@Database(entities = [ResponseModel::class, Favourite::class, AlertModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeatherDao() : WeatherDao

    companion object{
        @Volatile
        private var INSTANCE: WeatherDataBase?= null

        fun getInstance(context: Context): WeatherDataBase{
            return INSTANCE ?: synchronized(this){
                val instance= Room.databaseBuilder(context.applicationContext, WeatherDataBase::class.java,
                    "weather database")
                    .build()
                INSTANCE= instance
                instance
            }
        }
    }
}
