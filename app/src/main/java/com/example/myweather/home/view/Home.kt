package com.example.myweather.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.locations.GpsLocation
import com.example.myweather.locations.MapsFragment
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Home : Fragment(){

    lateinit var homeFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var dailyAdapter: HomeDailyAdapter
    lateinit var hourlyAdapter: HomeHourlyAdapter
    lateinit var binding: FragmentHomeBinding
    //31.217293724615672 //29.960048284658562

    lateinit var gps: GpsLocation

    lateinit var locSharedPref: String
    lateinit var windSharedPref: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gps= GpsLocation(requireContext())

        val args: HomeArgs by navArgs()
        var longitude= args.Longitude.toDouble()
        var latitude= args.Latitude.toDouble()

        homeFactory = HomeViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance()/*, ConcreteLocalDataSource(this)*/),
            requireContext(), gps)
        viewModel = ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]

        locSharedPref= activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE)?.getString("location", "none").toString()
        Log.i("TAG", "LocSharedPref: $locSharedPref")
       when (locSharedPref){
           "maps" -> viewModel.getRemoteWeather(latitude,longitude)
            "gps" ->  viewModel.getLocationByGps(requireContext())
            else -> Snackbar.make(view,"Choose Location type",
                Snackbar.LENGTH_LONG).setActionTextColor(resources.getColor(R.color.light_blue))
        }

        //Get Address
        viewModel.address.observe(viewLifecycleOwner){
            Log.i("TAG", "Addressss: $it")
            binding.locationTvHome.text= it
        }

        //Display Weather Data
        viewModel.current.observe(viewLifecycleOwner){
            if (it != null){
                binding.tempTvHome.text= "${it.current.temp.toInt().toString()} °C"
                binding.dateTvHome.text= getCurrentDay(it.current.dt.toInt())
                binding.descriptionHomeTv.text= it.current.weather[0].description
                when(it.current.weather[0].icon){
                    "01n" -> binding.imageView.setImageResource(R.drawable.monn)
                    "01d" -> binding.imageView.setImageResource(R.drawable.sunny)
                    "02d" -> binding.imageView.setImageResource(R.drawable.cloudy_sunny)
                    else ->
                        Glide.with(this)
                            .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}@2x.png")
                            .into(binding.imageView)
                }
                binding.cloudsTv.text= it.current.clouds.toString()
                binding.pressureTv.text= it.current.pressure.toString()
                binding.ultraTv.text= it.current.uvi.toString()
                binding.visibilityTv.text= it.current.visibility.toString()
                binding.windTv.text= it.current.windSpeed.toString()
                binding.humidityTv.text= it.current.humidity.toString()

                hourlyAdapter= HomeHourlyAdapter(requireContext(),it.hourly)
                binding.timeTempRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.timeTempRv.adapter = hourlyAdapter
                hourlyAdapter.notifyDataSetChanged()

                dailyAdapter= HomeDailyAdapter(requireContext(),it.daily)
                binding.daysTempRv.layoutManager = LinearLayoutManager(context)
                binding.daysTempRv.adapter = dailyAdapter
                dailyAdapter.notifyDataSetChanged()
            }
        }
    }

    fun getCurrentDay( dt: Int) : String{
        var date= Date(dt*1000L)
        var sdf= SimpleDateFormat("d")
        sdf.timeZone= TimeZone.getDefault()
        var formatedData=sdf.format(date)
        var intDay=formatedData.toInt()
        var calendar= Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH,intDay)
        var format= SimpleDateFormat("EEEE-dd-MMM")
        return format.format(calendar.time)
    }
}