package com.example.myweather.home.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.locations.GpsLocation
import com.example.myweather.model.Constants
import com.example.myweather.model.Repository
import com.example.myweather.model.ResponseModel
import com.example.myweather.network.ApiState
import com.example.myweather.network.NetworkChecker
import com.example.myweather.network.WeatherClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class Home : Fragment() {

    lateinit var homeFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var dailyAdapter: HomeDailyAdapter
    lateinit var hourlyAdapter: HomeHourlyAdapter
    lateinit var binding: FragmentHomeBinding

    lateinit var gps: GpsLocation

    lateinit var locSharedPref: String
    lateinit var windSharedPref: String
    lateinit var langharedPref: String
    lateinit var tempSharedPref: String
    var degree: String= ""
    lateinit var pd: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Home", "onCreate: ")
        activity?.onBackPressedDispatcher?.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("Home", "onCreateView: ")
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assign()

        val args: HomeArgs by navArgs()
        val longitude = args.Longitude.toDouble()
        val latitude = args.Latitude.toDouble()

        getSharedPrefernces()

        degree =
            when (tempSharedPref) {
                "metric" -> Constants.Celsius
                "standard" -> Constants.Kelvin
                "imperial" -> Constants.Fahrenheit
                else -> ""
            }

        if (NetworkChecker.checkForInternet(requireContext())) {
            Toast.makeText(requireContext(), "Connected", Toast.LENGTH_SHORT).show()

            when (locSharedPref) {
                "maps" -> viewModel.getRemoteWeather(latitude, longitude, langharedPref, tempSharedPref)
                "gps" -> viewModel.getLocationByGps(requireContext())
                else -> Snackbar.make(
                    view, "Choose Location type",
                    Snackbar.LENGTH_LONG
                ).setActionTextColor(resources.getColor(R.color.light_blue)).show()
            }

            //Set Weather Response
            lifecycleScope.launch {
                viewModel.homeStateFlow.collectLatest {
                    when(it){
                        is ApiState.Success->{
                            Log.i("Home", "Collect latest: ${it.data.timezone}")
                            viewModel.deleteCurrentWeather()
                            viewModel.addCurrentWeather(it.data)
                            setData(it.data)
                        }
                        else->{
                            Toast.makeText(requireContext(),"Loading", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        } else {
            Toast.makeText(requireContext(), "Disconnected", Toast.LENGTH_SHORT).show()
            Snackbar.make(
                view, "No Internet",
                Snackbar.LENGTH_INDEFINITE
            ).setActionTextColor(resources.getColor(R.color.light_blue)).show()

            viewModel.getLocaleWeather()
            lifecycleScope.launch {
                viewModel.homeStateFlow.collectLatest {
                    when(it){
                        is ApiState.Success->{
                            Log.i("Home", "Collect get LocalWeather: ${it.data.timezone} ")
                            setData(it.data)
                        }
                        else->{
                            Toast.makeText(requireContext(),"Loading", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }

        binding.swipeRefresh.setOnRefreshListener {
            findNavController().navigate(HomeDirections.actionHomeSelf())
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun assign() {
        pd = ProgressDialog(requireContext())
        gps = GpsLocation(requireContext())
        homeFactory = HomeViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(requireContext())),
            requireContext(), gps
        )
        viewModel = ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]
    }

    private fun getSharedPrefernces(){

        langharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("language", "standard").toString()
        tempSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("temp", "standard").toString()
        windSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("wind", "standard").toString()
        locSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("location", "none").toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDay(dt: Int): String {
        val date = Date(dt * 1000L)
        val sdf = SimpleDateFormat("d")
        sdf.timeZone = TimeZone.getDefault()
        val formattedData = sdf.format(date)
        val intDay = formattedData.toInt()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, intDay)
        val format = SimpleDateFormat("EEEE-dd-MMM")
        return format.format(calendar.time)
    }

    @SuppressLint("SetTextI18n")
    fun setData(response : ResponseModel){

        val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
        val editor = sharedPreference?.edit()
        editor?.putString("latitude",response.lat.toString())
        editor?.putString("longitude",response.lon.toString())
        editor?.putString("address",response.timezone)
        editor?.apply()

        response.current?.weather?.get(0).let { setBackGround(it?.icon as String) }
        val address=
            when(viewModel.getAddress(response.lat, response.lon)){
                "Undefined" -> response.timezone
                else -> viewModel.getAddress(response.lat, response.lon)
            }
        binding.locationTvHome.text= address

        binding.tempTvHome.text = response.current?.temp?.toInt().toString() + degree
        binding.dateTvHome.text = getCurrentDay(response.current?.dt!!.toInt())
        binding.descriptionHomeTv.text = response.current.weather[0].description
        when (response.current.weather[0].icon) {
            "01n" -> binding.imageView.setImageResource(R.drawable.monn)
            "01d" -> binding.imageView.setImageResource(R.drawable.sunny)
            "02d" -> binding.imageView.setImageResource(R.drawable.cloudy_sunny)
            else ->
                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/${response.current.weather[0].icon}@2x.png")
                    .into(binding.imageView)
        }
        binding.cloudsTv.text = response.current.clouds.toString() + "%"
        binding.pressureTv.text = response.current.pressure.toString() + "hPa"
        binding.ultraTv.text = response.current.uvi.toString()
        binding.visibilityTv.text = response.current.visibility.toString() + "m"
        binding.windTv.text = response.current.windSpeed.toString() + windSharedPref
        binding.humidityTv.text = response.current.humidity.toString() + "%"

        hourlyAdapter = HomeHourlyAdapter(requireContext(), response.hourly)
        binding.timeTempRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.timeTempRv.adapter = hourlyAdapter
        hourlyAdapter.notifyDataSetChanged()

        dailyAdapter = HomeDailyAdapter(requireContext(), response.daily)
        binding.daysTempRv.layoutManager = LinearLayoutManager(context)
        binding.daysTempRv.adapter = dailyAdapter
        dailyAdapter.notifyDataSetChanged()
    }

    @SuppressLint("ResourceAsColor")
    private fun setBackGround(icon : String){
        if (icon.endsWith("n")){
            binding.homeBck.background= resources.getDrawable(R.drawable.night_background)
        }else{
            if(icon.endsWith("d")){
                binding.homeBck.background= resources.getDrawable(R.drawable.morning_background)
            }
        }
    }
}