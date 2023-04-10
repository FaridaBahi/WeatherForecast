package com.example.myweather.alerts.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.R
import com.example.myweather.databinding.AlertItemBinding
import com.example.myweather.model.AlertModel
import com.google.android.material.snackbar.Snackbar

class AlertAdapter( private val onClick:(AlertModel)->Unit) :
    ListAdapter<AlertModel, AlertAdapter.AlertViewHolder>(AlertAdapter.AlertDiffUtil()) {

    lateinit var binding: AlertItemBinding

    class AlertViewHolder( var binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentObj= getItem(position)
        holder.binding.alertItemLocationTv.text= currentObj.address
        holder.binding.alertItemDateTv.text=
            "from ${currentObj.startDate?.substring(0, 16)} to ${currentObj.endDate?.substring(0, 16)}"
        holder.binding.delteAlertBtn.setOnClickListener { onClick(currentObj) }
        holder.binding.alertCardView.setOnClickListener{
            if (checkForInternet(it.context)){
                /*Navigation.findNavController(it)
                    .navigate(
                        FavouritesDirections
                        .actionFavouritesToFavPreview(currentObj.longitude.toFloat(),
                            currentObj.latitude.toFloat(), currentObj.name))*/
            }else{
                Snackbar.make(
                    it, "No Internet",
                    Snackbar.LENGTH_INDEFINITE
                ).setActionTextColor(it.resources.getColor(R.color.light_blue)).show()
            }
        }
    }

    class AlertDiffUtil: DiffUtil.ItemCallback<AlertModel>() {
        override fun areItemsTheSame(oldItem: AlertModel, newItem: AlertModel): Boolean {
            return oldItem.address == newItem.address
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: AlertModel, newItem: AlertModel): Boolean {
            return oldItem == newItem
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}