package com.example.myweather.home.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.myweather.locations.GpsLocation
import com.example.myweather.database.Converters
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val repo: RepositoryInterface,
    val context: Context,
    val gps: GpsLocation,
) : ViewModel() {
    private var _current: MutableLiveData<ResponseModel> = MutableLiveData<ResponseModel>()
    val current: LiveData<ResponseModel> = _current

    val temp: String= context.getSharedPreferences(
       "weatherApp", Context.MODE_PRIVATE)?.getString("temp", "standard").toString()
    val language: String= context.getSharedPreferences(
        "weatherApp", Context.MODE_PRIVATE)?.getString("language", "en").toString()

    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address


    fun getRemoteWeather(lat: Double, lon: Double, lang: String= "en", units: String= "standard", appid: String= "8beb73e4a526e79ac6ebf8f114f7ee43") {
        viewModelScope.launch(Dispatchers.IO) {
            _current.postValue(repo.getCurrentWeather(lat, lon, lang, units, appid))
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addressList= geocoder.getFromLocation(lon, lat, 1) as MutableList<Address>
                _address.postValue(addressList[0].adminArea + " " + addressList[0].countryName)
            }catch (e: Exception){
                Log.e("VM", "getRemoteWeather: ${e.message}")
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
        viewModelScope.launch(Dispatchers.IO) { repo.insertCurrentWeather(current)}
    }

    fun deleteCurrentWeather(){
        viewModelScope.launch (Dispatchers.IO){ repo.deleteCurrentWeather() }
    }

    fun getCurrentWeather(){
        viewModelScope.launch (Dispatchers.IO){
            _current.postValue(repo.getLocalCurrentWeather())
        }
    }

}
