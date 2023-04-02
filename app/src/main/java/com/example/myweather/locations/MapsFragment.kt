package com.example.myweather.locations

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.myweather.R
import com.example.myweather.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsFragment : Fragment(){


    /*private val callback = OnMapReadyCallback { googleMap ->
        //mMap = googleMap

        val latLon = LatLng(currentLocation.latitude, currentLocation.longitude)
        drawMarker(latLon)
        mMap.setOnMapClickListener (object :GoogleMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng) {
                //currentMarker?.remove()
                if (currentMarker != null){currentMarker?.remove()}
                val newLatLng= LatLng(p0.latitude, p0.longitude)
                drawMarker(newLatLng)
                binding.mapButton.visibility= View.VISIBLE
                binding.mapButton.setOnClickListener {
                    _data.postValue(newLatLng)
                    findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToHome())
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
    }*/
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it).title("Here")
                .snippet(getAddress(it.latitude, it.longitude)).draggable(true))
            goToLatLng(it.latitude,it.longitude)
        }
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private lateinit var fusedClient: FusedLocationProviderClient

    private var _data: MutableLiveData<LatLng> = MutableLiveData<LatLng>()
    val data: LiveData<LatLng> = _data
    private var _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> = _address

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        mapInitialize()
        return binding.root
    }

    private fun mapInitialize() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.smallestDisplacement = 16F
        locationRequest.fastestInterval = 3000

        binding.mapSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                || event.action == KeyEvent.KEYCODE_ENTER
            ) {
                if (!binding.mapSearch.text.isNullOrEmpty())
                    goToSearchedLocation()
            }
            false
        }
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun goToLatLng(latitude: Double, longitude: Double/*, float: Float*/) {
        val latLng = LatLng(latitude, longitude)
        //var update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, float)
        drawMarker(latLng)
        /*mMap.addMarker(MarkerOptions().position(latLng))
        mMap.animateCamera(update)*/
    }

    private fun drawMarker(latLon: LatLng) {
        val markerOptions = MarkerOptions().position(latLon).title("Here")
            .snippet(getAddress(latLon.latitude, latLon.longitude)).draggable(true)

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLon, 15f))
        mMap.addMarker(markerOptions)
    }

    private fun getAddress(lat: Double, Lon: Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, Lon, 1)
       // _data.postValue(LatLng(lat, Lon))
        _address.postValue(address?.get(0)?.getAddressLine(0))
        binding.mapButton.visibility= View.VISIBLE
        binding.mapButton.setOnClickListener {
            _data.postValue(LatLng(lat, Lon))
            findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToHome())
        }
        return address?.get(0)?.getAddressLine(0)
    }

    private fun goToSearchedLocation() {
        var requiredAddress = binding.mapSearch.text.toString()
        var list = Geocoder(requireContext()).getFromLocationName(requiredAddress,1)
        if (list!= null && list.size>0){
            val addresss: Address = list[0]
            goToLatLng(addresss.latitude,addresss.longitude)
        }
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*  val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
          mapFragment?.getMapAsync(callback)*/

        //fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //fetchLocation()
    }*/


/* fun fetchLocation() {
     if (ActivityCompat.checkSelfPermission(
             requireContext(),
             Manifest.permission.ACCESS_FINE_LOCATION
         ) != PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(
             requireContext(),
             Manifest.permission.ACCESS_COARSE_LOCATION
         ) != PackageManager.PERMISSION_GRANTED
     ) {
         ActivityCompat.requestPermissions(
             requireActivity(),
             arrayOf(
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.ACCESS_COARSE_LOCATION
             ), 1000
         )
         return
     }

     val task = fusedClient.lastLocation
     task.addOnSuccessListener {
         if (it != null){
             currentLocation= it
             val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
             mapFragment?.getMapAsync(this)
         }
     }
 }*/

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }*/


   /* override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        //fetchLocation()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedClient.lastLocation.addOnFailureListener {
            Log.e("Map", "onMapFailure: ${it.message}")
        }.addOnSuccessListener {
            val latling = LatLng(it.latitude, it.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latling, 17F))
        }

    }*/
}