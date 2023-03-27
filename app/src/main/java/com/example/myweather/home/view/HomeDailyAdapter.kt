package com.example.myweather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myweather.databinding.DailyItemBinding
import com.example.myweather.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class HomeDailyAdapter(var context: Context, var dailyList: List<Daily>)
    : RecyclerView.Adapter<HomeDailyAdapter.DailyViewHolder>() {

    lateinit var binding: DailyItemBinding

    class DailyViewHolder( var binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DailyItemBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val day: Daily= dailyList[position]
        holder.binding.dayDailyItemTv.text= getCurrentDay(day.dt.toInt())
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png")
            .into(holder.binding.iconDailyItem)
        //holder.binding.descriptionDailyItemTv.text = day.weather[0].description.toString()
       holder.binding.tempDailyItem.text= "${day.temp.min.toInt()}/${day.temp.max.toInt()}°C"
    }

    override fun getItemCount(): Int {
        return 6
    }

    fun getCurrentDay( dt: Int) : String{
        var date= Date(dt*1000L)
        var sdf= SimpleDateFormat("d")
        sdf.timeZone= TimeZone.getDefault()
        var formatedData=sdf.format(date)
        var intDay=formatedData.toInt()
        var calendar= Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH,intDay)
        var format= SimpleDateFormat("EEE")
        return format.format(calendar.time)
    }
}