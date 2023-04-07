package com.example.myweather

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val langharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("language", "standard").toString()

        when(langharedPref){
            "en" ->  localization("en")
            "ar" ->  localization("ar")
        }
    }

    fun localization(lang : String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale= locale
        resources.updateConfiguration(config,resources.displayMetrics)
    }
}