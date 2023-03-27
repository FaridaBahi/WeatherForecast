package com.example.myweather.home.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.myweather.GpsLocation
import com.example.myweather.model.Current
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: RepositoryInterface,
    val context: Context,
    val gps: GpsLocation
) : ViewModel() {
    private var _current: MutableLiveData<ResponseModel> = MutableLiveData<ResponseModel>()
    val current: LiveData<ResponseModel> = _current

    private var _lon_lat: MutableLiveData<LongitudeAndLatitude> = MutableLiveData()
    val lon_lat: LiveData<LongitudeAndLatitude> = _lon_lat

    /*private var _longitude: MutableLiveData<Double> = MutableLiveData()
    val longitude: LiveData<Double> = _longitude
    private var _latitude: MutableLiveData<Double> = MutableLiveData()
    val latitude: LiveData<Double> = _latitude*/

    init {
        getLocation(context)
    }

    fun getRemoteWeather(lat: Double, lon: Double, lang: String, appid: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _current.postValue(repo.getCurrentWeather(lat, lon, lang, appid, units))
        }
    }

    fun getLocation(context: Context) {
        gps.getLastLocation()
        gps.data.observe(context as LifecycleOwner, Observer {
            viewModelScope.launch(Dispatchers.IO) {
                _lon_lat.postValue(LongitudeAndLatitude(it.longitude, it.latitude))
            }
            getRemoteWeather(
                it.latitude,
                it.longitude,
                "en",
                "8beb73e4a526e79ac6ebf8f114f7ee43",
                "metric"
            )
        })
    }

    /*fun addCurrentWeather(product: Product){
        viewModelScope.launch(Dispatchers.IO) { repo.insertProduct(product) }
    }*/

}

data class LongitudeAndLatitude(val lon: Double, val lat: Double)
