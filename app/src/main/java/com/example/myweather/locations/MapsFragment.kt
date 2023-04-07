package com.example.myweather.locations

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myweather.R
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.databinding.FragmentMapsBinding
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.model.Favourite
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsFragment : Fragment(){

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

    lateinit var homeFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gps= GpsLocation(requireContext())

        homeFactory = HomeViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(requireContext())),
            requireContext(), gps)
        viewModel = ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]
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

    private fun goToLatLng(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        drawMarker(latLng)
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
        val args: MapsFragmentArgs by navArgs()
        binding.mapButton.visibility= View.VISIBLE
        binding.mapButton.setOnClickListener {
            binding.mapButton.animate().apply {
                duration= 1000
                rotationYBy(360f)
            }.withEndAction{
                if (args.fromFavourite == "favourite"){
                    var title = ""
                    binding.mapButton.text = "Save"
                    try {
                        title= address?.get(0)?.adminArea + " " + address?.get(0)?.countryName
                    }catch (e: Exception){
                        Log.i("TAG", "getAddress: Cant  get addres")
                    }
                    findNavController().navigate(MapsFragmentDirections.
                    actionMapsFragmentToFavourites(Lon.toFloat(), lat.toFloat(), title))
                }else{
                    findNavController().
                    navigate(MapsFragmentDirections.actionMapsFragmentToHome(Lon.toFloat(), lat.toFloat()))
                }
            }.start()
        }
        return address?.get(0)?.getAddressLine(0)
    }

    private fun goToSearchedLocation() {
        try {
            val requiredAddress = binding.mapSearch.text.toString()
            val list = Geocoder(requireContext()).getFromLocationName(requiredAddress,1)
            if (list!= null && list.size>0){
                val addresss: Address = list[0]
                goToLatLng(addresss.latitude,addresss.longitude)
            }
        }catch (e: Exception){
            Log.e("MAP", "goToSearchedLocation: ${e.message}" )
        }
    }
}