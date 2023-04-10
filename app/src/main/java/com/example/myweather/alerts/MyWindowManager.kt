package com.example.myweather.alerts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.TextView
import com.example.myweather.R
import com.example.myweather.model.AlertModel
import com.example.myweather.model.Alerts
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n", "CutPasteId")
class MyWindowManager(val context: Context, alertResponse: Alerts, alert: AlertModel) {

    private lateinit var wParam : WindowManager.LayoutParams
    private var windowMngr: WindowManager
    private var view: View
    private var layoutInflater: LayoutInflater

    private val mediaPlayer = MediaPlayer.create(context, R.raw.best_alarm)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // layout parameters wraps content and on top of app screen
            wParam = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }

        // inflating the view
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.notification, null)
        view.findViewById<TextView>(R.id.notification_title_tv).text = "Weather Alert"
        view.findViewById<TextView>(R.id.notification_event_tv).text = alertResponse.event +" in "+alert.address

        if(alertResponse.start != null) {
            view.findViewById<TextView>(R.id.notification_time_tv).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.notification_time_tv).text =
                SimpleDateFormat("hh:mm a",
                    Locale.getDefault()).format(Date(alertResponse.start!!.toLong()*1000)) +
                        SimpleDateFormat("hh:mm a",
                            Locale.getDefault()).format(Date(alertResponse.end!!.toLong()*1000))
        }

        //remove the view from the window
        view.findViewById<TextView>(R.id.notification_dismiss_tv).setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            close()
        }

        // Position of the window on screen
        wParam.width = WindowManager.LayoutParams.WRAP_CONTENT
        wParam.gravity = Gravity.TOP
        wParam.y = 10
        windowMngr = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDay(dt: Int) : String{
        val date= Date(dt*1000L)
        val sdf= SimpleDateFormat("d")
        sdf.timeZone= TimeZone.getDefault()
        val formatedData=sdf.format(date)
        val intDay=formatedData.toInt()
        val calendar= Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH,intDay)
        val format= SimpleDateFormat("EEE")
        return format.format(calendar.time)
    }

    fun open() {
        try {
            //if isn't already inflated
            if (view.windowToken == null) {
                if (view.parent == null) {
                    windowMngr.addView(view, wParam)
                }
            }
            if(!mediaPlayer.isPlaying){
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            Log.e("WindowManager", e.message.toString())
        }
    }

    private fun close() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
            view.invalidate()
            (view.parent as ViewGroup).removeAllViews()

        } catch (e: Exception) {
            Log.e("WindowManager", e.message.toString())
        }
    }

}