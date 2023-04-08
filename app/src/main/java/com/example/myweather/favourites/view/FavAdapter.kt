package com.example.myweather.favourites.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.MainActivity
import com.example.myweather.R
import com.example.myweather.databinding.FavItemBinding
import com.example.myweather.model.Favourite
import com.google.android.material.snackbar.Snackbar

class FavAdapter( private val onClick:(Favourite)->Unit) :
    ListAdapter<Favourite, FavAdapter.FavViewHolder>(FavDiffUtil()) {

    lateinit var binding: FavItemBinding

    class FavViewHolder( var binding: FavItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavItemBinding.inflate(inflater, parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val currentObj= getItem(position)
        holder.binding.favTv.text= currentObj.name
        holder.binding.deleteBtnFav.setOnClickListener { onClick(currentObj) }
        holder.binding.favCardView.setOnClickListener{
            if (checkForInternet(it.context)){
                findNavController(it)
                    .navigate(FavouritesDirections
                        .actionFavouritesToHome(currentObj.longitude.toFloat(),
                            currentObj.latitude.toFloat()))
            }else{
                Snackbar.make(
                    it, "No Internet",
                    Snackbar.LENGTH_INDEFINITE
                ).setActionTextColor(it.resources.getColor(R.color.light_blue)).show()
            }
        }
    }

    class FavDiffUtil: DiffUtil.ItemCallback<Favourite>() {
        override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
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