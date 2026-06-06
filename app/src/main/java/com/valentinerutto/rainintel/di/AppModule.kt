package com.valentinerutto.rainintel.di

import com.valentinerutto.rainintel.MyApplication
import com.valentinerutto.rainintel.data.WeatherRepository
import com.valentinerutto.rainintel.data.local.RainIntelDatabase
import com.valentinerutto.rainintel.data.network.ApiService
import com.valentinerutto.rainintel.ui.WeatherViewModel
import com.valentinerutto.rainintel.util.RetrofitClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

 val appModule = module {
    single { MyApplication.INSTANCE }
   single { WeatherRepository(get(), get()) }
     viewModel { WeatherViewModel(get()) }
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
    single { get<RainIntelDatabase>().treeAnalysisDao() }
}

