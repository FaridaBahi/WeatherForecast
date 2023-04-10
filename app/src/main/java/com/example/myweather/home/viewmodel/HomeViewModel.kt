package com.example.myweather.home.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.myweather.locations.GpsLocation
import com.example.myweather.database.Converters
import com.example.myweather.model.APP_ID
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.model.ResponseModel
import com.example.myweather.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeViewModel(
    private val repo: RepositoryInterface,
    val context: Context,
    val gps: GpsLocation,
) : ViewModel() {

    private var _homeStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    var homeStateFlow: StateFlow<ApiState> = _homeStateFlow

    val temp: String= context.getSharedPreferences(
       "weatherApp", Context.MODE_PRIVATE)?.getString("temp", "standard").toString()
    val language: String= context.getSharedPreferences(
        "weatherApp", Context.MODE_PRIVATE)?.getString("language", "en").toString()

    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address


    fun getRemoteWeather(lat: Double, lon: Double, lang: String= "en", units: String= "standard", appid: String= APP_ID) {

        viewModelScope.launch (Dispatchers.IO){
            val response= repo.getCurrentWeather(lat, lon, lang, units, appid)
            withContext(Dispatchers.Main){
                response.catch {
                    _homeStateFlow.value= ApiState.Failure(it)
                }.collect{
                    Log.i("ViewModel", "getRemoteWeather: ${it.timezone}")
                    _homeStateFlow.value= ApiState.Success(it)
                }
            }
        }
    }

    fun getAddress(lat: Double, lon: Double) : String{
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

    fun getLocationByGps(context: Context) {
        gps.getLastLocation()
        gps.data.observe(context as LifecycleOwner, Observer {
            getRemoteWeather(
                it.latitude,
                it.longitude,
                language,
                temp
            )
        })
    }

    fun addCurrentWeather(current : ResponseModel){
        Log.i("ViewModel", "addCurrentWeather: ${current.timezone} ")
        //deleteCurrentWeather()
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCurrentWeather(current)
        }
    }

    fun deleteCurrentWeather(){
        Log.i("ViewModel", "deleteCurrentWeather: ")
        viewModelScope.launch (Dispatchers.IO){ repo.deleteCurrentWeather() }
    }

    fun getLocaleWeather(){
        viewModelScope.launch (Dispatchers.IO){
            val response= repo.getLocalCurrentWeather()
            withContext(Dispatchers.Main){
                response.catch {
                    _homeStateFlow.value= ApiState.Failure(it)
                }.collect{
                    Log.i("ViewModel", "getLocaleWeather: ${it.timezone}")
                    _homeStateFlow.value= ApiState.Success(it)
                }
            }
        }
    }

}
