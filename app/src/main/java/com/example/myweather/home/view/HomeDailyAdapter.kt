package com.example.myweather.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.DailyItemBinding
import com.example.myweather.model.Constants
import com.example.myweather.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class HomeDailyAdapter(var context: Context, var dailyList: List<Daily>)
    : RecyclerView.Adapter<HomeDailyAdapter.DailyViewHolder>() {

    lateinit var binding: DailyItemBinding
    var tempSharedPref= context.getSharedPreferences(
        "weatherApp", Context.MODE_PRIVATE)?.getString("temp", "°C").toString()
    var degree: String= ""

    class DailyViewHolder( var binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        tempDegree(tempSharedPref)
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DailyItemBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val day: Daily= dailyList[position]
        holder.binding.dayDailyItemTv.text= getCurrentDay(day.dt.toInt())
        when(day.weather[0].icon){
            "01n" -> holder.binding.iconDailyItem.setImageResource(R.drawable.monn)
            "01d" -> holder.binding.iconDailyItem.setImageResource(R.drawable.sunny)
            "02d" -> holder.binding.iconDailyItem.setImageResource(R.drawable.cloudy_sunny)
            else -> Glide.with(context)
            .load("https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png")
            .into(holder.binding.iconDailyItem)
        }

        holder.binding.descriptionDailyItemTv.text = day.weather[0].description
       holder.binding.tempDailyItem.text= "${day.temp?.min?.toInt()}/${day.temp?.max?.toInt()}" + degree
    }

    override fun getItemCount(): Int {
        return 6
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDay(dt: Int) : String{
        val date= Date(dt*1000L)
        val sdf= SimpleDateFormat("d")
        sdf.timeZone= TimeZone.getDefault()
        val formatedData=sdf.format(date)
        val intDay=formatedData.toInt()
        val calendar= Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH,intDay)
        val format= SimpleDateFormat("EEE")
        return format.format(calendar.time)
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