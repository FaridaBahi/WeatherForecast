package com.example.myweather.database

import android.content.Context
import android.util.Log
import com.example.myweather.model.AlertModel
import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context : Context) : LocalSource {
   private val dao: WeatherDao by lazy {
        val db: WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getWeatherDao()
    }

    override fun getLocalCurrentWeather(): Flow<ResponseModel> {
        Log.i("ConcreteLocalSource", "getLocalCurrentWeather: by dao ")
       return dao.getCurrent()
    }

    override suspend fun deleteCurrentWeather() {
        Log.i("ConcreteLocalSource", "deleteCurrentWeather: by dao ")
        dao.deleteCurrent()
    }

    override suspend fun insertCurrentWeather(data: ResponseModel) {
        Log.i("ConcreteLocalSource", "insertCurrentWeather: ${data.timezone}")
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

    override fun getStoredAlerts(): Flow<List<AlertModel>> {
        return dao.getStoredAlerts()
    }

    override suspend fun insertAlert(alert: AlertModel): Long {
        return dao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertModel) {
        dao.deleteAlert(alert)
    }


}