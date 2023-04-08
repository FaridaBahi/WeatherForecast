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
    //31.217293724615672 //29.960048284658562

    lateinit var gps: GpsLocation

    lateinit var locSharedPref: String
    lateinit var windSharedPref: String
    lateinit var langharedPref: String
    lateinit var tempSharedPref: String
    var degree: String= ""
    var address: String= ""
    lateinit var pd: ProgressDialog


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

        if (checkForInternet(requireContext())) {
            Toast.makeText(requireContext(), "Connected", Toast.LENGTH_SHORT).show()

            when (locSharedPref) {
                "maps" -> viewModel.getRemoteWeather(latitude, longitude, langharedPref, tempSharedPref)
                "gps" -> viewModel.getLocationByGps(requireContext())
                else -> Snackbar.make(
                    view, "Choose Location type",
                    Snackbar.LENGTH_LONG
                ).setActionTextColor(resources.getColor(R.color.light_blue)).show()
            }

            //Set Address
            viewModel.address.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    address= it
                }
            }

            //Set Weather Response
            /*try{
                viewModel.current.observe(viewLifecycleOwner) {
                    if (it != null) {
                        viewModel.deleteCurrentWeather()
                        viewModel.addCurrentWeather(it)
                        setData(it)
                    }
                }

            }catch (e: Exception){
                Log.i("TAG", "Home onViewCreated: ${e.message}")
            }*/
            lifecycleScope.launch {
                viewModel.homeStateFlow.collectLatest {
                    when(it){
                        is ApiState.Loading ->{
                            //pd.setMessage("loading")
                            //pd.show()
                        }
                        is ApiState.Success->{
                            //pd.dismiss()
                            Log.i("Home", "Collect latest: ${it.data.timezone}")
                            viewModel.addCurrentWeather(it.data)
                            setData(it.data)
                        }
                        else->{
                            //pd.dismiss()
                            //Toast.makeText(requireContext(),"Check your connection", Toast.LENGTH_SHORT).show()
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
            /*viewModel.getLocaleWeather()
            viewModel.current.observe(viewLifecycleOwner){
                if (it != null){
                    setData(it)
                }
            }*/
            viewModel.getLocaleWeather()
            lifecycleScope.launch {
                viewModel.homeStateFlow.collectLatest {
                    when(it){
                        is ApiState.Loading ->{
                            //pd.setMessage("loading")
                            //pd.show()
                        }
                        is ApiState.Success->{
                            //pd.dismiss()
                            setData(it.data)
                        }
                        else->{
                            //pd.dismiss()
                            //Toast.makeText(requireContext(),"Check your connection", Toast.LENGTH_SHORT).show()
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

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M or greater we need to use the
        // NetworkCapabilities to check what type of network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
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

        response?.current?.weather?.get(0).let { setBackGround(it?.icon as String) }
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
        when (icon) {
            "01n" -> binding.homeBck.background= resources.getDrawable(R.drawable.night_background)
            "01d" -> binding.homeBck.background= resources.getDrawable(R.drawable.morning_background)
            else -> binding.homeBck.setBackgroundColor(R.color.grey)
        }
    }
}