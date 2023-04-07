package com.example.myweather.settings.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myweather.databinding.FragmentSettingsBinding
import java.util.Locale

class Settings : Fragment() {

    lateinit var binding: FragmentSettingsBinding

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

        val langharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("language", "standard").toString()
        val tempSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("temp", "standard").toString()
        val windSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("wind", "standard").toString()
        val locSharedPref = activity?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("location", "none").toString()

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

        binding.gpsRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp", Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("location","gps")
            editor?.commit()
        }

        binding.mapRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("location","maps")
            editor?.commit()

            findNavController().navigate(SettingsDirections.actionSettingsToMapsFragment())
        }

        binding.arabicRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("language","ar")
            editor?.commit()

            val locale = Locale("ar")
            Locale.setDefault(locale)
            val config= Configuration()
            config.locale= locale
            resources.updateConfiguration(config,resources.displayMetrics)
        }

        binding.englishRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("language","en")
            editor?.commit()

            val locale = Locale("en")
            Locale.setDefault(locale)
            val config= Configuration()
            config.locale= locale
            resources.updateConfiguration(config,resources.displayMetrics)
        }

        binding.celsiusRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("temp","metric")
            editor?.commit()
            binding.meterRadioButton.isChecked= true
        }

        binding.fahrenheitRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("temp","imperial")
            editor?.commit()
            binding.milesRadioButton.isChecked= true
        }

        binding.kelvinRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("temp","standard")
            editor?.commit()
            binding.meterRadioButton.isChecked= true
        }

        binding.meterRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("wind","m/s")
            editor?.commit()
        }

        binding.milesRadioButton.setOnClickListener {
            val sharedPreference =  activity?.getSharedPreferences("weatherApp",Context.MODE_PRIVATE)
            val editor = sharedPreference?.edit()
            editor?.putString("wind","mph")
            editor?.commit()
            binding.fahrenheitRadioButton.isChecked= true
        }
    }
}