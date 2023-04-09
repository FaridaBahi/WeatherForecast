package com.example.myweather.favourites.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FavPreviewViewModel(private val repo: RepositoryInterface,val  context: Context) : ViewModel() {
    private var _favStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    var favStateFlow: StateFlow<ApiState> = _favStateFlow

    val temp: String= context.getSharedPreferences(
        "weatherApp", Context.MODE_PRIVATE)?.getString("temp", "standard").toString()
    val language: String= context.getSharedPreferences(
        "weatherApp", Context.MODE_PRIVATE)?.getString("language", "en").toString()

    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address

    fun getRemoteWeather(lat: Double, lon: Double, lang: String= "en", units: String= "standard", appid: String= "8beb73e4a526e79ac6ebf8f114f7ee43") {
        viewModelScope.launch (Dispatchers.IO){
            val response= repo.getCurrentWeather(lat, lon, lang, units, appid)
            withContext(Dispatchers.Main){
                response.catch {
                    _favStateFlow.value= ApiState.Failure(it)
                }.collect{
                    Log.i("ViewModel", "getRemoteWeather: ${it.timezone}")
                    _favStateFlow.value= ApiState.Success(it)
                }
            }
        }
        getAddress(lat,lon)
    }

    private fun getAddress(lat: Double, lon: Double) : String{
        var address :String = "Undefined"
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList= geocoder.getFromLocation(lon, lat, 1) as MutableList<Address>
            address= addressList[0].adminArea + " " + addressList[0].countryName
        }catch (e: Exception){
            Log.e("VM", "get address: ${e.message}")
        }
        return address
    }
}