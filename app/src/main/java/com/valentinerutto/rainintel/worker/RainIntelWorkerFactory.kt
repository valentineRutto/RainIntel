package com.valentinerutto.rainintel.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.valentinerutto.rainintel.data.WeatherRepository
import com.valentinerutto.rainintel.util.WeatherNotificationHelper
import com.valentinerutto.rainintel.util.location.DeviceLocationProvider

class RainIntelWorkerFactory(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: DeviceLocationProvider,
    private val notificationHelper: WeatherNotificationHelper
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WeatherAlertWorker::class.java.name -> WeatherAlertWorker(
                appContext = appContext,
                workerParams = workerParameters,
                weatherRepository = weatherRepository,
                locationProvider = locationProvider,
                notificationHelper = notificationHelper
            )

            else -> null
        }
    }
}
