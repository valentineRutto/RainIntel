package com.valentinerutto.farmvision.ui

import androidx.lifecycle.ViewModel
import com.valentinerutto.farmvision.data.WeatherRepository
import com.valentinerutto.farmvision.data.network.response.WeatherResponse

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel()  {

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return repository.getWeather(lat, lon)
    }

}