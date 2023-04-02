package com.example.myweather.locations

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myweather.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


/*class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedClient: FusedLocationProviderClient
    lateinit var currentLocation: Location
    var currentMarker: Marker?= null

    private var _data : MutableLiveData<LatLng> = MutableLiveData<LatLng>()
    val data: LiveData<LatLng> = _data
    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
    }

    fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1000
            )
            return
        }

        val task = fusedClient.lastLocation
        task.addOnSuccessListener {
            if (it != null){
                currentLocation= it
                val mapFragment = supportFragmentManager
                    .findFragmentById(com.example.myweather.R.id.mapFragment) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLon = LatLng(currentLocation.latitude, currentLocation.longitude)
        drawMarker(latLon)
       mMap.setOnMapClickListener (object :GoogleMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng) {
                currentMarker?.remove()
                //if (currentMarker != null){currentMarker?.remove()}
                val newLatLng= LatLng(p0.latitude, p0.longitude)
                drawMarker(newLatLng)
                binding.mapButton.visibility= View.VISIBLE
                binding.mapButton.setOnClickListener {
                    _data.postValue(newLatLng)
                    //findNavController( com.example.myweather.R.id.nav_graph).navigate(MapsActivityDirections.actionMapsActivityToHome())
                    /*startActivity(Intent(this, MainActivity::class.java))
                    val navController: NavController =
                        Navigation.findNavController(MainActivity(), com.example.myweather.R.id.nav_graph)
                    navController.navigateUp()
                    navController.navigate(com.example.myweather.R.id.home)*/
                }
            }

        })
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {
                TODO("Not yet implemented")
            }

            override fun onMarkerDragEnd(p0: Marker) {
                currentMarker?.remove()
                //if (currentMarker != null){currentMarker?.remove()}
                val newLatLng= LatLng(p0.position.latitude, p0.position.longitude)
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000 -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

    private fun drawMarker(latLon: LatLng){
        val markerOptions= MarkerOptions().position(latLon).title("Here")
            .snippet(getAddress(latLon.latitude, latLon.longitude)).draggable(true)

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLon, 15f))
        mMap.addMarker(markerOptions)
    }

    private fun getAddress(lat:Double, Lon:Double): String?{
        val geoCoder= Geocoder(this, Locale.getDefault())
        val address= geoCoder.getFromLocation(lat, Lon, 1)
        _data.postValue(LatLng(lat, Lon))
        _address.postValue(address?.get(0)?.getAddressLine(0))
        return address?.get(0)?.getAddressLine(0)
    }
}*/