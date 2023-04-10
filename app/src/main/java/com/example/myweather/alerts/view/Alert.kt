package com.example.myweather.alerts.view

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweather.R
import com.example.myweather.alerts.viewmodel.AlertViewModel
import com.example.myweather.alerts.viewmodel.AlertViewModelFactory
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.database.roomState.AlertRoomState
import com.example.myweather.database.roomState.FavRoomState
import com.example.myweather.databinding.FragmentAlertBinding
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Alert : Fragment() {

    lateinit var binding: FragmentAlertBinding
    lateinit var alertAdapter: AlertAdapter
    lateinit var alertFactory: AlertViewModelFactory
    lateinit var viewModel: AlertViewModel
    lateinit var pd: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //(activity as AppCompatActivity).supportActionBar?.show()
        activity?.onBackPressedDispatcher?.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(AlertDirections.actionAlertToHome())
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAlertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        //Deleting
        alertAdapter= AlertAdapter {
            viewModel.deleteAlert(it)
            showSnackBar(view)
        }
        displaying()


    }

    private fun setup(){
        alertFactory = AlertViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance(), ConcreteLocalSource(requireContext()))
        )
        viewModel = ViewModelProvider(this, alertFactory)[AlertViewModel::class.java]
        pd = ProgressDialog(requireContext())
        binding.alertFab.setOnClickListener {
            AlertDialogFragment.newInstance()
                .show(requireActivity().supportFragmentManager, "AlertDialogFragment")
        }
    }
    private fun displaying(){
        binding.alertRv.apply {
            this.adapter= alertAdapter
            layoutManager= LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            viewModel.alertStateFlow.collectLatest {
                when(it){
                    is AlertRoomState.Loading ->{
                        pd.setMessage("loading")
                        pd.show()
                    }
                    is AlertRoomState.Success->{
                        pd.dismiss()
                        alertAdapter.submitList(it.data)
                        alertAdapter.notifyDataSetChanged()
                    }
                    else->{
                        pd.dismiss()
                        Toast.makeText(requireContext(),"Can't get alerts", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun showSnackBar(view: View){
        Snackbar.make(
            view, "Deleted",
            Snackbar.LENGTH_SHORT
        ).setActionTextColor(resources.getColor(R.color.light_blue)).show()
    }

}