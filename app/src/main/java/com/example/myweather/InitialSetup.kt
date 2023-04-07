package com.example.myweather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InitialSetup : Fragment() {

    private var selectedLocationIndex: Int = 0
    private val location = arrayOf("GPS", "Map")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDialog()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intial_setup, container, false)
    }


    private fun showDialog() {

        var selectedFruits = location[selectedLocationIndex]
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Please, Choose your location ")
            .setSingleChoiceItems(location, selectedLocationIndex) { _, which ->
                selectedLocationIndex = which
                selectedFruits = location[which]
            }
            .setPositiveButton("Ok") { _, _ ->
                if (selectedFruits == "GPS") {
                    val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
                    val editor = sharedPreference?.edit()
                    editor?.putString("location","gps")
                    editor?.apply()

                    val action =
                        InitialSetupDirections.actionIntialSetupToHome()
                    findNavController().navigate(action)

                }else{
                    val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
                    val editor = sharedPreference?.edit()
                    editor?.putString("location","maps")
                    editor?.apply()

                    val action=
                        InitialSetupDirections.actionIntialSetupToMapsFragment()
                    findNavController().navigate(action)
                }
                Toast.makeText(requireActivity(), "$selectedFruits Selected", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}