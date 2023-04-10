package com.example.myweather.alerts

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.model.*
import com.example.myweather.network.NetworkChecker
import com.example.myweather.network.WeatherClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlertWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private var alertModel: AlertModel? = null
    private var alert: Alerts? =
        Alerts(event = "No Alerts", description = "No Alerts")

    private var weatherRoot: ResponseModel? = null

    private val repo: RepositoryInterface = Repository.getInstance(WeatherClient(), ConcreteLocalSource(context))

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {

            startAlertWork()

            Result.success()
        } catch (e: Exception) {
            Log.i("AlertWorker", "doWork: " + e.message)
            Result.failure()
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private suspend fun startAlertWork() {
        val strAlertGson = inputData.getString("alertModel")
        alertModel = Gson().fromJson(strAlertGson, AlertModel::class.java)

        if (Date() <= getDate(alertModel?.endDate!!)) {
            getWeatherData()
            deleteAlert()
        } else {
            deleteAlert()
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun showAlert() {
        startService()
    }


    private fun startService() {

        val alertServiceIntent = Intent(applicationContext, AlertService::class.java)
        alertServiceIntent.putExtra("alertModel", alertModel)
        alertServiceIntent.putExtra("alertApi", alert)

        if (Settings.canDrawOverlays(applicationContext)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(alertServiceIntent)
            } else {
                applicationContext.startService(alertServiceIntent)
            }
        }

    }


    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getWeatherData() {

        if(NetworkChecker.checkForInternet(applicationContext)) {
            repo.getCurrentWeather(
                alertModel?.latitude?.toDouble() as Double,
                alertModel?.longitude?.toDouble() as Double,
                "en",
                "metric",
                APP_ID
            )
                .catch {
                    Log.i("AlertWorker", "getWeatherData: " + it.message)
                    showAlert()
                }.collect {
                    weatherRoot = it
                    getWeatherAlerts(it)
                    showAlert()
                }
        }else{
            showAlert()
        }
    }

    private fun getWeatherAlerts(response: ResponseModel) {
        Log.i("AlertWorker", "getWeatherAlerts: alerts  " + weatherRoot!!.alerts)
        alert = response.alerts[0]
    }

    private suspend fun deleteAlert() {
        if (Date().day >= getDate(alertModel?.endDate!!)!!.day) {
            repo.deleteAlert(alertModel!!)
            WorkManager.getInstance().cancelAllWorkByTag(alertModel!!.currentTime)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(strDate : String) : Date?{
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        try {
            return format.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

}