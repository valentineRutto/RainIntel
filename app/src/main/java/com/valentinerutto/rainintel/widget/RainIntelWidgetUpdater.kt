package com.valentinerutto.rainintel.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.valentinerutto.rainintel.MainActivity
import com.valentinerutto.rainintel.R
import com.valentinerutto.rainintel.data.local.WeatherDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RainIntelWidgetUpdater(
    private val context: Context,
    private val weatherDao: WeatherDao,
    private val locationStore: WidgetLocationStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun updateAll() {
        scope.launch {
            updateAllWidgets()
        }
    }

    suspend fun updateAllWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, RainIntelWidgetProvider::class.java)
        )

        widgetIds.forEach { widgetId ->
            appWidgetManager.updateAppWidget(widgetId, buildRemoteViews())
        }
    }

    private suspend fun buildRemoteViews(): RemoteViews {
        val latestWeather = weatherDao.getCurrentLatest()
        val views = RemoteViews(context.packageName, R.layout.widget_weather)

        views.setTextViewText(R.id.widget_app_name, context.getString(R.string.app_name))
        views.setTextViewText(R.id.widget_location_name, locationStore.getLocationName())

        if (latestWeather == null) {
            views.setTextViewText(R.id.widget_temperature, "--°")
            views.setTextViewText(R.id.widget_status, "Open RainIntel to load weather")
        } else {
            views.setTextViewText(R.id.widget_temperature, "${latestWeather.temperature.toInt()}°")
            views.setTextViewText(R.id.widget_status, "Tap to open RainIntel")
        }

        views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent())
        return views
    }

    private fun openAppPendingIntent(): PendingIntent {
        var flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            flags
        )
    }

    private companion object {
        const val OPEN_APP_REQUEST_CODE = 3001
    }
}
