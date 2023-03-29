package com.example.myweather

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myweather.locations.MapsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IntialSetup : Fragment() {

    val itemList = arrayOf("Gps", "Map")

    private var selectedLocationIndex: Int = 0
    private val location = arrayOf("GPS", "Map")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDailog()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intial_setup, container, false)
    }


    fun showDailog() {

        var selectedFruits = location[selectedLocationIndex]
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Please, Choose your location ")
            .setSingleChoiceItems(location, selectedLocationIndex) { dialog_, which ->
                selectedLocationIndex = which
                selectedFruits = location[which]
            }
            .setPositiveButton("Ok") { dialog, which ->
                if (selectedFruits.equals("GPS")) {
                    val action =
                        IntialSetupDirections.actionIntialSetupToHome()
                    findNavController().navigate(action)

                }else{
                    startActivity(Intent(requireContext(), MapsActivity::class.java))
                }
                Toast.makeText(requireActivity(), "$selectedFruits Selected", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}