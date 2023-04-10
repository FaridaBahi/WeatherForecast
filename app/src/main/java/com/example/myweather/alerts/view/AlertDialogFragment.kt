package com.example.myweather.alerts.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.myweather.alerts.AlertWorker
import com.example.myweather.alerts.viewmodel.AlertDialogFragmentViewModel
import com.example.myweather.alerts.viewmodel.AlertDialogViewModelFactory
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.databinding.AlertDialogSetupBinding
import com.example.myweather.model.AlertModel
import com.example.myweather.model.Repository
import com.example.myweather.network.WeatherClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertDialogFragment : DialogFragment() {

    private var _binding: AlertDialogSetupBinding? = null
    private val binding get() = _binding!!

    private var mDay: Int? = null
    private var mMinute: Int? = null
    private var mYear: Int? = null
    private var mMonth: Int? = null
    private var mHour: Int? = null

    private var startDate: String? = null
    private var endDate: String? = null
    private var startTime: String? = null
    private var endTime: String? = null

    private var fullStartDate: String? = null
    private var fullEndDate: String? = null

    private lateinit var alertModel: AlertModel

    private var alertEnabled: Boolean = true


    private val latitudeShP by lazy {
        requireActivity()?.getSharedPreferences(
                "weatherApp", Context.MODE_PRIVATE
            )?.getString("latitude", "undetected").toString()
    }
    private val longitudeShP by lazy {
        requireActivity()?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("longitude", "undetected").toString()
    }
    private val addressShP by lazy {
        requireActivity()?.getSharedPreferences(
            "weatherApp", Context.MODE_PRIVATE
        )?.getString("address", "undetected").toString()
    }

    private val viewModel: AlertDialogFragmentViewModel by lazy {

        val factory = AlertDialogViewModelFactory(
            Repository.getInstance(
                WeatherClient(), ConcreteLocalSource(requireContext())
            )
        )

        ViewModelProvider(this, factory)[AlertDialogFragmentViewModel::class.java]
    }

    companion object {

        fun newInstance(): AlertDialogFragment {
            val args = Bundle()
            val fragment = AlertDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AlertDialogSetupBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleAlertRadioGroup()

        binding.fromDateTextview.setOnClickListener {
            showDateDialog(it as TextView)
        }
        binding.fromTimeTextview.setOnClickListener {
            showTimeDialog(it as TextView)
        }

        binding.toDateTv.setOnClickListener {
            showDateDialog(it as TextView)
        }
        binding.toTimeTextview.setOnClickListener {
            showTimeDialog(it as TextView)
        }

        binding.cancelTv.setOnClickListener {
            dismiss()
        }

        binding.okTv.setOnClickListener {
            handleAlertClick()
        }

    }

    private fun handleAlertClick() {
        val currentDate: String=
            SimpleDateFormat("hh:mm a   E, MMM dd, yyyy", Locale.getDefault()).format(Date())
        if (Settings.canDrawOverlays(requireContext())) {
            if (checkAlertTerms()) {
                alertModel = AlertModel(
                    latitude = latitudeShP,
                    longitude = longitudeShP,
                    currentTime = currentDate,
                    startDate = fullStartDate,
                    endDate = fullEndDate,
                    address = addressShP,
                    alertEnabled = alertEnabled
                )
                insetAlert()
            }
        } else {
            checkOverlayPermission()
        }
    }

    private fun insetAlert() {
        viewModel.insertAlert(alertModel)

        lifecycleScope.launch {
            viewModel.alertStateFlow.collectLatest {
                setUpAlertWorker()
                finishSave()
            }
        }
    }

    private fun finishSave() {
        dismiss()
        Toast.makeText(requireContext(), "Alert Saved", Toast.LENGTH_LONG).show()
    }


    private fun setUpAlertWorker() {
        val data = Data.Builder()
        data.putString("alertModel", Gson().toJson(alertModel))
        data.build()

        val delay = getDelay(fullStartDate!!)

        val workRequest = PeriodicWorkRequestBuilder<AlertWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data.build())
            .addTag(alertModel.currentTime)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            alertModel.currentTime,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun showDateDialog(dateTextView: TextView) {

        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, year, monthOfYear, dayOfMonth ->

                val strDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                dateTextView.text = strDate

                when (dateTextView.id) {
                    com.example.myweather.R.id.from_date_textview -> startDate = strDate
                    else -> endDate = strDate
                }

            },
            mYear!!,
            mMonth!!,
            mDay!!
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
        datePickerDialog.show()
    }

    private fun showTimeDialog(timeTextView: TextView) {
        val c2: Calendar = Calendar.getInstance()
        mHour = c2.get(Calendar.HOUR_OF_DAY)
        mMinute = c2.get(Calendar.MINUTE)


        val timePickerDialog = TimePickerDialog(
            requireActivity(), { _, hourOfDay, minute ->

                val time = get12HourFormat(hourOfDay, minute)
                timeTextView.text = time

                when (timeTextView.id) {
                    com.example.myweather.R.id.from_time_textview -> startTime = time
                    else -> endTime = time
                }

            }, mHour!!, mMinute!!, false
        )
        timePickerDialog.show()
    }

    @SuppressLint("DefaultLocale")
    private fun get12HourFormat(hourOfDay: Int, minute: Int): String {
        val isPM = hourOfDay >= 12
        return java.lang.String.format(
            "%02d:%02d %s",
            if (hourOfDay === 12 || hourOfDay === 0) 12 else hourOfDay % 12,
            minute,
            if (isPM) "PM" else "AM"
        )
    }

    private fun getDelay(dateStr: String): Long {
        val currentDate=
            SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(dateStr)
        val calendar: Calendar = Calendar.getInstance()
        return currentDate!!.time - calendar.timeInMillis
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    "Give app permission to display alert over apps",
                    Toast.LENGTH_LONG
                ).show()
                // send user to the device settings
                val myIntent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireContext().packageName)
                )
                startActivity(myIntent)
            }
        }
    }

    private fun checkAlertTerms(): Boolean {
        if (startDate == null) {
            Toast.makeText(requireContext(), "Select start date", Toast.LENGTH_LONG).show()
            return false
        } else if (endDate == null) {
            Toast.makeText(requireContext(), "Select end date", Toast.LENGTH_LONG).show()
            return false
        } else if (startTime == null) {
            Toast.makeText(requireContext(), "Select start time", Toast.LENGTH_LONG).show()
            return false
        } else if (endTime == null) {
            Toast.makeText(requireContext(), "Select end time", Toast.LENGTH_LONG).show()
            return false
        } else if (!checkAlertDurationValidation()) return false
        return true
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkAlertDurationValidation(): Boolean {
        fullStartDate = "$startDate $startTime"
        fullEndDate = "$endDate $endTime"

        if (fullEndDate != null && fullStartDate != null) {
            val endDate= SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(fullEndDate)
            val startDate= SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(fullStartDate)
            if (endDate >= startDate) {
                return true
            } else {
                Toast.makeText(
                    requireContext(),
                    "End date should be after start date",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return false
    }

    private fun handleAlertRadioGroup() {
        binding.alertRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                com.example.myweather.R.id.alert_radio_button -> {
                    alertEnabled = true
                }
                com.example.myweather.R.id.notification_radio_button -> {
                    alertEnabled = false
                }
            }
        }
    }


}