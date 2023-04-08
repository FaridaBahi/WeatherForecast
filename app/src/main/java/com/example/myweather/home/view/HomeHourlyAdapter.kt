package com.example.myweather.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.HourlyItemBinding
import com.example.myweather.model.Constants
import com.example.myweather.model.Current
import java.text.SimpleDateFormat
import java.util.*

class HomeHourlyAdapter(var context: Context, var hourlyList: List<Current>)
    : RecyclerView.Adapter<HomeHourlyAdapter.HourlyViewHolder>() {

    lateinit var binding: HourlyItemBinding
    var tempSharedPref= context.getSharedPreferences(
    "weatherApp", Context.MODE_PRIVATE)?.getString("temp", "°C").toString()
    var degree: String= ""

    class HourlyViewHolder( var binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        tempDegree(tempSharedPref)

        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyItemBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hour: Current= hourlyList[position]
        holder.binding.tempHourlyItem.text= hour.temp.toInt().toString() + degree
        holder.binding.hourTv.text= getCurrentTime(hour.dt.toInt()) + "00"
        when(hour.weather[0].icon){
            "01n" -> holder.binding.iconHourlyItem.setImageResource(R.drawable.monn)
            "01d" -> holder.binding.iconHourlyItem.setImageResource(R.drawable.sunny)
            "02d" -> holder.binding.iconHourlyItem.setImageResource(R.drawable.cloudy_sunny)
            else -> Glide.with(context)
                .load("https://openweathermap.org/img/wn/${hour.weather[0].icon}@2x.png")
                .into(holder.binding.iconHourlyItem)
        }
    }

    override fun getItemCount(): Int {
        return 24
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(dt: Int): String {
        val date= Date(dt*1000L)
        val sdf= SimpleDateFormat("hh:mm a")
        sdf.timeZone= TimeZone.getDefault()
        return sdf.format(date)

    }

    private fun tempDegree(tempShP: String){
        degree =
            when (tempShP) {
                "metric" -> Constants.Celsius
                "standard" -> Constants.Kelvin
                "imperial" -> Constants.Fahrenheit
                else -> ""
            }
    }
}