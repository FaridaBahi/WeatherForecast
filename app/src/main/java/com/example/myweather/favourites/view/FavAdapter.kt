package com.example.myweather.favourites.view

import android.annotation.SuppressLint
import android.content.Context
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
            findNavController(it)
                .navigate(FavouritesDirections
                    .actionFavouritesToHome(currentObj.longitude.toFloat(),
                    currentObj.latitude.toFloat()))
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
}