package com.example.myweather.database

import android.content.Context
import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context : Context) : LocalSource {
   private val dao: WeatherDao by lazy {
        val db: WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getWeatherDao()
    }

    override fun getLocalCurrentWeather(): Flow<ResponseModel> {
       return dao.getCurrent()
    }

    override suspend fun deleteCurrentWeather() {
        dao.deleteCurrent()
    }

    override suspend fun insertCurrentWeather(data: ResponseModel) {
        dao.insertCurrent(data)
    }

    override fun getAllSavedLocation(): Flow<List<Favourite>> {
        return dao.getFavourites()
    }

    override suspend fun insertLocation(location: Favourite) {
       dao.insertFavourite(location)
    }

    override suspend fun removeSavedLocation(location: Favourite) {
        dao.deleteFavourites(location)
    }
}