package com.example.myweather.settings.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.myweather.InitialSetupDirections
import com.example.myweather.databinding.FragmentSettingsBinding
import com.example.myweather.home.view.HomeDirections
import java.util.Locale

class Settings : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    var langharedPref : String? = null
    var tempSharedPref : String? = null
    var windSharedPref : String? = null
    var locSharedPref  : String? = null
    var notifySharedPref : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Settings", "onCreate: ")
        activity?.onBackPressedDispatcher?.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(SettingsDirections.actionSettingsToHome())
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSharedPref()
        initializeSettings()

        binding.gpsRadioButton.setOnClickListener {
            editSharedPref("location","gps")
        }

        binding.mapRadioButton.setOnClickListener {
            editSharedPref("location","maps")
            findNavController().navigate(SettingsDirections.actionSettingsToMapsFragment())
        }

        binding.arabicRadioButton.setOnClickListener {
            editSharedPref("language","ar")
            localization("ar")

            //activity?.recreate()
            //findNavController().navigate(InitialSetupDirections.actionInitialSetupToSettings())
        findNavController().navigate(SettingsDirections.actionSettingsSelf())
        }

        binding.englishRadioButton.setOnClickListener {
            editSharedPref("language","en")
            localization("en")
        findNavController().navigate(SettingsDirections.actionSettingsSelf())
        }

        binding.celsiusRadioButton.setOnClickListener {
            editSharedPref("temp","metric")
            binding.meterRadioButton.isChecked= true
        }

        binding.fahrenheitRadioButton.setOnClickListener {
            editSharedPref("temp","imperial")
            binding.milesRadioButton.isChecked= true
        }

        binding.kelvinRadioButton.setOnClickListener {
            editSharedPref("temp","standard")
            binding.meterRadioButton.isChecked= true
        }

        binding.meterRadioButton.setOnClickListener {
            editSharedPref("wind","m/s")
        }

        binding.milesRadioButton.setOnClickListener {
            editSharedPref("wind","mph")
            binding.fahrenheitRadioButton.isChecked= true
        }

        binding.enableRadioButton.setOnClickListener {
            editSharedPref("notification","enable")
        }

        binding.disbaleRadioButton.setOnClickListener {
            editSharedPref("notification","disable")
        }
    }

    private fun initializeSettings() {
        when(langharedPref){
            "en" -> binding.englishRadioButton.isChecked= true
            "ar" -> binding.arabicRadioButton.isChecked= true
        }
        when(tempSharedPref){
            "metric" -> binding.celsiusRadioButton.isChecked= true
            "imperial" -> binding.fahrenheitRadioButton.isChecked= true
            "standard" -> binding.kelvinRadioButton.isChecked= true
        }
        when(windSharedPref){
            "m/s" -> binding.meterRadioButton.isChecked= true
            "mph" -> binding.milesRadioButton.isChecked= true
        }
        when(locSharedPref){
            "maps" -> binding.mapRadioButton.isChecked= true
            "gps" -> binding.gpsRadioButton.isChecked= true
        }
        when(notifySharedPref){
            "enable" -> binding.enableRadioButton.isChecked= true
            "disable" -> binding.disbaleRadioButton.isChecked= true
        }
    }

    private fun localization(lang : String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale= locale
        resources.updateConfiguration(config,resources.displayMetrics)
    }

    private fun getSharedPref(){
        langharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("language", "standard").toString()
        tempSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("temp", "standard").toString()
        windSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("wind", "standard").toString()
        locSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("location", "none").toString()
        notifySharedPref= activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("notification", "disable").toString()
    }

    private fun editSharedPref(category: String, value: String){
        val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
        val editor = sharedPreference?.edit()
        editor?.putString(category,value)
        editor?.commit()
    }


}