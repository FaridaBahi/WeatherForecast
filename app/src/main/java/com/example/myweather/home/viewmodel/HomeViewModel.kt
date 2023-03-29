package com.example.myweather.home.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.myweather.locations.GpsLocation
import com.example.myweather.locations.MapsActivity
import com.example.myweather.model.Current
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.model.ResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: RepositoryInterface,
    val context: Context,
    val gps: GpsLocation,
    val maps: MapsActivity
) : ViewModel() {
    private var _current: MutableLiveData<ResponseModel> = MutableLiveData<ResponseModel>()
    val current: LiveData<ResponseModel> = _current

    private var _lon_lat: MutableLiveData<LongitudeAndLatitude> = MutableLiveData()
    val lon_lat: LiveData<LongitudeAndLatitude> = _lon_lat

    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address


    fun getRemoteWeather(lat: Double, lon: Double, lang: String, appid: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _current.postValue(repo.getCurrentWeather(lat, lon, lang, appid, units))
        }
    }

    fun getLocationByGps(context: Context) {
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

    fun getLocationByMaps(){
        maps.fetchLocation()
        maps.data.observe(context as LifecycleOwner, Observer {
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

        maps.address.observe(context as LifecycleOwner, Observer {
            viewModelScope.launch(Dispatchers.IO){
                _address.postValue(it)
            }
        })

    }

    /*fun addCurrentWeather(product: Product){
        viewModelScope.launch(Dispatchers.IO) { repo.insertProduct(product) }
    }*/

}

data class LongitudeAndLatitude(val lon: Double, val lat: Double)
