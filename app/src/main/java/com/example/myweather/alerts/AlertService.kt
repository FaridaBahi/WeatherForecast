package com.example.myweather.alerts

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.myweather.MainActivity
import com.example.myweather.R
import com.example.myweather.model.AlertModel
import com.example.myweather.model.Alerts

const val NOTIFICATION_CHANNEL_ID = "507"

class AlertService : Service() {

    private var alert : Alerts? = null
    private var alertModel : AlertModel? = null

    private val notifySharedPref by lazy {
        applicationContext?.getSharedPreferences(
                "weatherApp", Context.MODE_PRIVATE
            )?.getString("notification", "disable").toString()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent != null) {
            alert = intent.getSerializableExtra("alertApi") as Alerts
            alertModel = intent.getSerializableExtra("alertModel") as AlertModel
        }

        if(notifySharedPref == "enable") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotification()
            else startForeground(1, Notification())
        }

        if(alertModel!!.alertEnabled) {
            val window = MyWindowManager(this, alert!!,alertModel!!)
            window.open()
        }

        return START_NOT_STICKY
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!
        manager.createNotificationChannel(chan)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)


        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Weather Alert")
            .setContentText(alert?.event+ " in "+alertModel?.address)
            .setSmallIcon(R.drawable.logo)
            .setSound(
                Uri.parse("android.resource://"
                    + applicationContext.packageName + "/" + R.raw.sms_stone))
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(2, notification)
    }

}