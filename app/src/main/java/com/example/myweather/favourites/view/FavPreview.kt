package com.example.myweather.favourites.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.databinding.FragmentFavPreviewBinding
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.favourites.viewmodel.FavPreviewViewModel
import com.example.myweather.favourites.viewmodel.FavPreviewViewModelFactory
import com.example.myweather.favourites.viewmodel.FavViewModelFactory
import com.example.myweather.home.view.HomeArgs
import com.example.myweather.home.view.HomeDailyAdapter
import com.example.myweather.home.view.HomeHourlyAdapter
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.locations.GpsLocation
import com.example.myweather.model.Constants
import com.example.myweather.model.Favourite
import com.example.myweather.model.Repository
import com.example.myweather.model.ResponseModel
import com.example.myweather.network.ApiState
import com.example.myweather.network.WeatherClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FavPreview : Fragment() {
    lateinit var favFactory: FavPreviewViewModelFactory
    lateinit var viewModel: FavPreviewViewModel
    lateinit var dailyAdapter: HomeDailyAdapter
    lateinit var hourlyAdapter: HomeHourlyAdapter
    lateinit var binding: FragmentFavPreviewBinding

    lateinit var windSharedPref: String
    lateinit var langharedPref: String
    lateinit var tempSharedPref: String
    var degree: String= ""
    var title: String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        activity?.onBackPressedDispatcher?.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(FavPreviewDirections.actionFavPreviewToFavourites())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFavPreviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        val args: FavPreviewArgs by navArgs()
        val lon= args.Longitude.toDouble()
        val lat= args.Latitude.toDouble()
        title= args.title

        degree =
            when (tempSharedPref) {
                "metric" -> Constants.Celsius
                "standard" -> Constants.Kelvin
                "imperial" -> Constants.Fahrenheit
                else -> ""
            }

        viewModel.getRemoteWeather(lon, lat, langharedPref, tempSharedPref)

        lifecycleScope.launch {
            viewModel.favStateFlow.collectLatest {
                when(it){
                    is ApiState.Success->{
                        setData(it.data)
                    }
                    else->{
                        Toast.makeText(requireContext(),"Loading", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setUp() {
        favFactory = FavPreviewViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(requireContext())),
            requireContext()
        )
        viewModel = ViewModelProvider(this, favFactory)[FavPreviewViewModel::class.java]

        langharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("language", "en").toString()
        tempSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("temp", "standard").toString()
        windSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("wind", "standard").toString()
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

        binding.locationTvFav.text= title

        binding.tempTvFav.text = response.current?.temp?.toInt().toString() + degree
        binding.dateTvFav.text = getCurrentDay(response.current?.dt!!.toInt())
        binding.descriptionFavTv.text = response.current.weather[0].description
        when (response.current.weather[0].icon) {
            "01n" -> binding.imageViewFav.setImageResource(R.drawable.monn)
            "01d" -> binding.imageViewFav.setImageResource(R.drawable.sunny)
            "02d" -> binding.imageViewFav.setImageResource(R.drawable.cloudy_sunny)
            else ->
                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/${response.current.weather[0].icon}@2x.png")
                    .into(binding.imageViewFav)
        }
        binding.cloudsTvFav.text = response.current.clouds.toString() + "%"
        binding.pressureTvFav.text = response.current.pressure.toString() + "hPa"
        binding.ultraTvFav.text = response.current.uvi.toString()
        binding.visibilityTvFav.text = response.current.visibility.toString() + "m"
        binding.windTvFav.text = response.current.windSpeed.toString() + windSharedPref
        binding.humidityTvFav.text = response.current.humidity.toString() + "%"

        hourlyAdapter = HomeHourlyAdapter(requireContext(), response.hourly)
        binding.timeTempRvFav.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.timeTempRvFav.adapter = hourlyAdapter
        hourlyAdapter.notifyDataSetChanged()

        dailyAdapter = HomeDailyAdapter(requireContext(), response.daily)
        binding.daysTempRvFav.layoutManager = LinearLayoutManager(context)
        binding.daysTempRvFav.adapter = dailyAdapter
        dailyAdapter.notifyDataSetChanged()
    }

    @SuppressLint("ResourceAsColor")
    private fun setBackGround(icon : String){
        if (icon.endsWith("n")){
            binding.favPreviewBck.background= resources.getDrawable(R.drawable.night_background)
        }else{
            if(icon.endsWith("d")){
                binding.favPreviewBck.background= resources.getDrawable(R.drawable.morning_background)
            }
        }
    }
}