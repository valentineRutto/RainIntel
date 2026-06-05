package com.valentinerutto.farmvision.data.network

import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("weather-geo")
    suspend fun getWeatherForecast(): WeatherResponse

    @POST("trees/analyze")
    suspend fun getAnalyzedImage() : TreeAnalysisResponse




}