package com.valentinerutto.farmvision.data

import com.valentinerutto.farmvision.data.network.ApiService
import com.valentinerutto.farmvision.data.network.response.WeatherResponse

class WeatherRepository(private val apiService: ApiService) {


    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return apiService.getWeather(lat, lon)
    }


}