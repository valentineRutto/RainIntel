package com.valentinerutto.rainintel.di

import androidx.work.WorkManager
import com.valentinerutto.rainintel.MyApplication
import com.valentinerutto.rainintel.data.WeatherRepository
import com.valentinerutto.rainintel.data.local.CitySeeder
import com.valentinerutto.rainintel.data.local.RainIntelDatabase
import com.valentinerutto.rainintel.data.network.ApiService
import com.valentinerutto.rainintel.ui.WeatherViewModel
import com.valentinerutto.rainintel.util.RetrofitClient
import com.valentinerutto.rainintel.util.WeatherNotificationHelper
import com.valentinerutto.rainintel.util.location.DeviceLocationProvider
import com.valentinerutto.rainintel.widget.RainIntelWidgetUpdater
import com.valentinerutto.rainintel.widget.WidgetLocationStore
import com.valentinerutto.rainintel.worker.RainIntelWorkerFactory
import com.valentinerutto.rainintel.worker.WeatherAlertWorkScheduler
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

	 val appModule = module {
	    single { MyApplication.INSTANCE }
	    single { DeviceLocationProvider(get<MyApplication>()) }
	    single { WeatherNotificationHelper(get<MyApplication>()) }
		    single { WorkManager.getInstance(get<MyApplication>()) }
		    single { RainIntelWorkerFactory(get(), get(), get()) }
		    single { WeatherAlertWorkScheduler(get()) }
		    single { WidgetLocationStore(get<MyApplication>()) }
		    single { RainIntelWidgetUpdater(get<MyApplication>(), get(), get()) }
		    single { WeatherRepository(get(), cityDao = get(), weatherDao = get()) }
		    viewModel { WeatherViewModel(get(), get()) }
		 }


 val networkingModule = module {

        single { RetrofitClient.provideOkHttpClient() }
        single { RetrofitClient.provideRetrofit(RetrofitClient.BASE_URL, get()) }

        single {
            get<Retrofit>().create(ApiService::class.java)
        }
    }

val databaseModule = module {
    single { RainIntelDatabase.getDatabase(get()) }
    single { get<RainIntelDatabase>().weatherDao() }
    single { get<RainIntelDatabase>().cityDao() }
    single { CitySeeder(get(), get()) }
}
