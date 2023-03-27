package com.example.myweather.home.view

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myweather.GpsLocation
import com.example.myweather.R
import com.example.myweather.SendLocation
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import java.text.SimpleDateFormat
import java.util.*

class Home : Fragment(){

    lateinit var homeFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var dailyAdapter: HomeDailyAdapter
    lateinit var hourlyAdapter: HomeHourlyAdapter
    lateinit var binding: FragmentHomeBinding
    //31.217293724615672 //29.960048284658562

    lateinit var geocoder: Geocoder
    lateinit var gps: GpsLocation
    lateinit var addressList: MutableList<Address>

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
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        homeFactory = HomeViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance()/*, ConcreteLocalDataSource(this)*/),
            requireContext(), gps)
        viewModel = ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]

        //Get Address by GeoCoder
        viewModel.lon_lat.observe(viewLifecycleOwner){
            geocoder = Geocoder(requireContext(), Locale.getDefault())
            addressList= geocoder.getFromLocation(it.lon, it.lat, 1) as MutableList<Address>
            binding.locationTvHome.text= addressList[0].adminArea + " " + addressList[0].countryName
        }

        //Display Data
        viewModel.current.observe(viewLifecycleOwner){
            if (it != null){
                binding.tempTvHome.text= "${it.current.temp.toInt().toString()} °C"
                binding.dateTvHome.text= getCurrentDay(it.current.dt.toInt())
                //binding.descriptionHomeTv.text= it.current.weather[0].description.toString()
                //println("Description: ${it.current.weather[0].description}")
                when(it.current.weather[0].icon){
                    "01n" -> binding.imageView.setImageResource(R.drawable.monn)
                    "01d" -> binding.imageView.setImageResource(R.drawable.sunny)
                    "02d" -> binding.imageView.setImageResource(R.drawable.cloudy_sunny)
                    else ->
                        Glide.with(this)
                            .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}@2x.png")
                            .into(binding.imageView)
                }
                println("Current: ${it.current.weather[0].icon}")
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