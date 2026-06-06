package com.valentinerutto.rainintel.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.valentinerutto.rainintel.data.WeatherRepository
import com.valentinerutto.rainintel.util.WeatherAlertAnalyzer
import com.valentinerutto.rainintel.util.WeatherNotificationHelper
import com.valentinerutto.rainintel.util.location.DeviceLocationProvider
import com.valentinerutto.rainintel.util.location.LocationResult

class WeatherAlertWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepository,
    private val locationProvider: DeviceLocationProvider,
    private val notificationHelper: WeatherNotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val location = when (val result = locationProvider.getCurrentLocation()) {
            is LocationResult.Success -> result.location
            LocationResult.PermissionDenied,
            LocationResult.LocationUnavailable -> return Result.retry()
            is LocationResult.Error -> return Result.retry()
        }

        return runCatching {
            val weatherResponse = weatherRepository.refreshWeatherForLocation(
                lat = location.latitude,
                lon = location.longitude
            )

            notificationHelper.createNotificationChannel()
            WeatherAlertAnalyzer.buildAlerts(weatherResponse.hourly.orEmpty()).forEach { alert ->
                notificationHelper.showWeatherAlert(alert)
            }

            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}
