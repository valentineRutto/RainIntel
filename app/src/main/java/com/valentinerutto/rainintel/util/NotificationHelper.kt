package com.valentinerutto.rainintel.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.valentinerutto.rainintel.MainActivity
import com.valentinerutto.rainintel.R

class WeatherNotificationHelper(
    private val context: Context
) {
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            WEATHER_ALERT_CHANNEL_ID,
            WEATHER_ALERT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Weather alerts for rain and severe conditions"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showWeatherAlert(alert: WeatherAlert) {
        if (!canPostNotifications()) return

        val contentIntent = PendingIntent.getActivity(
            context,
            alert.notificationId,
            Intent(context, MainActivity::class.java).apply {
                action = ACTION_OPEN_WEATHER_ALERT
                putExtra(EXTRA_NOTIFICATION_ID, alert.notificationId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            WEATHER_ALERT_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(alert.title)
            .setContentText(alert.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(alert.message))
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            alert.notificationId,
            notification
        )
    }

    private fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val WEATHER_ALERT_CHANNEL_ID = "weather_alerts"
        const val ACTION_OPEN_WEATHER_ALERT = "com.valentinerutto.rainintel.OPEN_WEATHER_ALERT"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val UNKNOWN_NOTIFICATION_ID = -1
        private const val WEATHER_ALERT_CHANNEL_NAME = "Weather alerts"
    }
}
