package com.valentinerutto.rainintel.util

import com.valentinerutto.rainintel.data.network.response.Hourly

object WeatherAlertAnalyzer {

    fun buildAlerts(
        hourly: List<Hourly?>
    ): List<WeatherAlert> {
        val rainAlert = hourly
            .filterNotNull()
            .firstOrNull { (it.precipitation_probability ?: 0) >= RAIN_ALERT_THRESHOLD }
            ?.let {
                WeatherAlert(
                    notificationId = RAIN_NOTIFICATION_ID,
                    title = "🌧 Rain Expected",
                    message = "There is a ${it.precipitation_probability}% chance of rain at ${it.time.orEmpty()} today. Carry an umbrella."
                )
            }

        return listOfNotNull(rainAlert)
    }

    private const val RAIN_ALERT_THRESHOLD = 60
    private const val RAIN_NOTIFICATION_ID = 2001
}

data class WeatherAlert(
    val notificationId: Int,
    val title: String,
    val message: String
)
