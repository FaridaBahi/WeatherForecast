package com.example.myweather.favourites.view

import android.app.ProgressDialog
import android.os.Binder
import android.os.Bundle
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
import com.example.myweather.R
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.database.roomState.FavRoomState
import com.example.myweather.databinding.FragmentFavouritesBinding
import com.example.myweather.favourites.viewmodel.FavViewModel
import com.example.myweather.favourites.viewmodel.FavViewModelFactory
import com.example.myweather.home.view.HomeArgs
import com.example.myweather.home.view.HomeDailyAdapter
import com.example.myweather.home.viewmodel.HomeViewModel
import com.example.myweather.home.viewmodel.HomeViewModelFactory
import com.example.myweather.model.Favourite
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Favourites : Fragment() {

    lateinit var binding: FragmentFavouritesBinding
    lateinit var favAdapter: FavAdapter
    lateinit var favFactory: FavViewModelFactory
    lateinit var viewModel: FavViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFavouritesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pd = ProgressDialog(requireContext())
        val args: FavouritesArgs by navArgs()
        val lon= args.Longitude.toDouble()
        val lat= args.Latitude.toDouble()
        val title= args.title
        val favObj= Favourite(title,lon,lat)

        favFactory = FavViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(requireContext())),
            requireContext()
        )
        viewModel = ViewModelProvider(this, favFactory)[FavViewModel::class.java]
        viewModel.getLocaleSavedLocation()

        //Inserting
       if (lon != null && lat != null && title != null){
            //viewModel.insertToFavourites(objectFromMap)
           viewModel.insertToFavourites(favObj)
       }

       //Deleting
        favAdapter = FavAdapter{
            viewModel.delete(it)
        }

        //Displaying
        binding.favRv.apply {
            this.adapter= favAdapter
            layoutManager= LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            viewModel.favStateFlow.collectLatest {
                when(it){
                    is FavRoomState.Loading ->{
                        pd.setMessage("loading")
                        pd.show()
                    }
                    is FavRoomState.Success->{
                        pd.dismiss()
                        favAdapter.submitList(it.data)
                        favAdapter.notifyDataSetChanged()
                    }
                    else->{
                        pd.dismiss()
                        Toast.makeText(requireContext(),"Can't get favourites", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(FavouritesDirections.actionFavouritesToMapsFragment("favourite"))
        }
    }
}