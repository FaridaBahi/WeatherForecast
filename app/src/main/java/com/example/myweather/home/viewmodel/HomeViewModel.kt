package com.example.myweather.home.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.myweather.locations.GpsLocation
import com.example.myweather.model.RepositoryInterface
import com.example.myweather.model.ResponseModel
import com.google.android.gms.maps.model.LatLng
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

   // private var _lon_lat: MutableLiveData<LatLng> = MutableLiveData()
    //val lon_lat: LiveData<LatLng> = _lon_lat
    val temp: String= context?.getSharedPreferences(
       "weatherApp", Context.MODE_PRIVATE)?.getString("temp", "metric").toString()
    val language: String= context?.getSharedPreferences(
        "weatherApp", Context.MODE_PRIVATE)?.getString("lang", "en").toString()

    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address


    fun getRemoteWeather(lat: Double, lon: Double, lang: String= language, units: String= temp, appid: String= "8beb73e4a526e79ac6ebf8f114f7ee43") {
        viewModelScope.launch(Dispatchers.IO) {
            _current.postValue(repo.getCurrentWeather(lat, lon, lang, units, appid))
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList= geocoder.getFromLocation(lon, lat, 1) as MutableList<Address>
            _address.postValue(addressList[0].adminArea + " " + addressList[0].countryName)
        }
    }

    fun getLocationByGps(context: Context) {
        gps.getLastLocation()
        gps.data.observe(context as LifecycleOwner, Observer {
           /* viewModelScope.launch(Dispatchers.IO) {
                _lon_lat.postValue(LatLng(it.longitude, it.latitude))
            }*/
            getRemoteWeather(
                it.latitude,
                it.longitude
            )
        })
    }

    /*fun getLocationByMaps(){
        //maps.mapInitialize()
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

    }*/

    /*fun addCurrentWeather(product: Product){
        viewModelScope.launch(Dispatchers.IO) { repo.insertProduct(product) }
    }*/

}
