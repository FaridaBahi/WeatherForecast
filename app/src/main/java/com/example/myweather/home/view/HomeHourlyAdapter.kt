package com.example.myweather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myweather.databinding.DailyItemBinding
import com.example.myweather.databinding.HourlyItemBinding
import com.example.myweather.model.Current
import com.example.myweather.model.Daily
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List

class HomeHourlyAdapter(var context: Context, var hourlyList: List<Current>)
    : RecyclerView.Adapter<HomeHourlyAdapter.HourlyViewHolder>() {

    lateinit var binding: HourlyItemBinding

    class HourlyViewHolder( var binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyItemBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hour: Current= hourlyList[position]
        holder.binding.tempHourlyItem.text= hour.temp.toInt().toString() + "°C"
        holder.binding.hourTv.text= getCurrentTime(hour.dt.toInt()) + "00"
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${hour.weather[0].icon}@2x.png")
            .into(holder.binding.iconHourlyItem)
    }

    override fun getItemCount(): Int {
        return 24
    }

    private fun getCurrentTime(dt: Int): String {
        var date= Date(dt*1000L)
        var sdf= SimpleDateFormat("hh:mm a")
        sdf.timeZone= TimeZone.getDefault()
        return sdf.format(date)

    }
}