package com.example.myweather.database

import androidx.room.*
import com.example.myweather.model.Favourite
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM current")
    fun getCurrent() : Flow<ResponseModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(current : ResponseModel)

    @Query("DELETE FROM current")
    suspend fun deleteCurrent()

    @Query("SELECT * FROM favourites")
    fun getFavourites() : Flow<List<Favourite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(location : Favourite)

    @Delete
    suspend fun deleteFavourites(location : Favourite)
}