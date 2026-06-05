package com.valentinerutto.farmvision.di

import com.valentinerutto.farmvision.MyApplication
import com.valentinerutto.farmvision.data.WeatherRepository
import com.valentinerutto.farmvision.data.local.FarmVisionDatabase
import com.valentinerutto.farmvision.data.network.ApiService
import com.valentinerutto.farmvision.ui.WeatherViewModel
import com.valentinerutto.farmvision.util.RetrofitClient
import com.valentinerutto.farmvision.util.RetrofitClient.createOkClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

 val appModule = module {
    single { MyApplication.INSTANCE }
   single { WeatherRepository(get()) }
     viewModel { WeatherViewModel(get()) }
 }


 val networkingModule = module {
        single { RetrofitClient.provideOkHttpClient() }
        single { RetrofitClient.provideRetrofit(RetrofitClient.BASE_URL, get()) }

        single { createOkClient() }

        single {
            get<Retrofit>().create(ApiService::class.java)
        }
    }

val databaseModule = module {
    single { FarmVisionDatabase.getDatabase(get()) }
    single { get<FarmVisionDatabase>().farmVisionDao() }
}



