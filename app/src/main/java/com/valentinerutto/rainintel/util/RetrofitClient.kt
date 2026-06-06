package com.valentinerutto.rainintel.util

import com.valentinerutto.rainintel.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    const val BASE_URL = "https://api.weather-ai.co/v1/"

    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {

        "application/json".toMediaType()
        return Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader("Authorization")

            level = if (BuildConfig.DEBUG){
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }

        }
    }


    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header(
                            "Authorization",
                            "Bearer ${BuildConfig.WEATHER_API_KEY}"
                        )
                        .build()
                    )
            }
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }



}
