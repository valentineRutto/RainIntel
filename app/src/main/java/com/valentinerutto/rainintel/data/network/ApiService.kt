package com.valentinerutto.rainintel.data.network

import com.valentinerutto.rainintel.data.network.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getWeather( @Query("lat") lat: Double,
                                        @Query("lon") lon: Double,
                                        @Query("days") days: Int = 7,
                                        @Query("ai") ai: Boolean = true,
                                        @Query("units") units: String = "metric",
                                        @Query("lang") lang: String = "en"): WeatherResponse
}
