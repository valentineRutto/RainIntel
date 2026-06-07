package com.valentinerutto.rainintel

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.valentinerutto.rainintel.data.local.CitySeeder
import com.valentinerutto.rainintel.di.appModule
import com.valentinerutto.rainintel.di.databaseModule
import com.valentinerutto.rainintel.di.networkingModule
import com.valentinerutto.rainintel.util.WeatherNotificationHelper
import com.valentinerutto.rainintel.widget.RainIntelWidgetUpdater
import com.valentinerutto.rainintel.worker.RainIntelWorkerFactory
import com.valentinerutto.rainintel.worker.WeatherAlertWorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.ext.android.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {
    private val citySeeder: CitySeeder by inject()
    private val notificationHelper: WeatherNotificationHelper by inject()
    private val workerFactory: RainIntelWorkerFactory by inject()
    private val weatherAlertWorkScheduler: WeatherAlertWorkScheduler by inject()
    private val widgetUpdater: RainIntelWidgetUpdater by inject()
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        lateinit var INSTANCE: MyApplication
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        startKoin{
            androidLogger( level = Level.DEBUG)
            androidContext(this@MyApplication)

            modules(appModule,networkingModule, databaseModule)


        }

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )

        notificationHelper.createNotificationChannel()
        weatherAlertWorkScheduler.schedule()
        widgetUpdater.updateAll()

        applicationScope.launch {
            runCatching {
                citySeeder.seedIfNeeded()
            }.onFailure { throwable ->
                Log.e("MyApplication", "Unable to seed cities", throwable)
            }
        }
    }
}
