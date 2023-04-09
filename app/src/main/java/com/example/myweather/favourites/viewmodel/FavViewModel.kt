package com.example.myweather.favourites.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.database.roomState.FavRoomState
import com.example.myweather.model.Favourite
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavViewModel(
    private val repo: RepositoryInterface,
    //val context: Context
): ViewModel()
{

    private var _favStateFlow: MutableStateFlow<FavRoomState> = MutableStateFlow(FavRoomState.Loading)
    var favStateFlow: StateFlow<FavRoomState> = _favStateFlow

    fun getLocaleSavedLocation(){
        viewModelScope.launch(Dispatchers.IO) {
            val locations= repo.getAllSavedLocation()
            withContext(Dispatchers.Main){
                locations.catch {
                    _favStateFlow.value= FavRoomState.Failure(it)
                }.collect{
                    _favStateFlow.value= FavRoomState.Success(it)
                }
            }
        }
    }

    fun insertToFavourites(location : Favourite){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertLocation(location)
        }
    }

    fun delete(location: Favourite){
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeSavedLocation(location)
            getLocaleSavedLocation()
        }
    }
}