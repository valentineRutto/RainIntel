package com.valentinerutto.farmvision.ui

import androidx.lifecycle.ViewModel
import com.valentinerutto.farmvision.data.WeatherRepository
import com.valentinerutto.farmvision.data.models.WeatherUiData

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel()  {

    suspend fun getWeather(lat: Double, lon: Double): WeatherUiData {
        return repository.getWeather(lat, lon)
    }

}
