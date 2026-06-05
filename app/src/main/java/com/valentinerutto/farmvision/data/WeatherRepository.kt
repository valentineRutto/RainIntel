package com.valentinerutto.farmvision.data

import com.valentinerutto.farmvision.data.local.mapToDailyWeatherEntity
import com.valentinerutto.farmvision.data.local.toWeatherEntity
import com.valentinerutto.farmvision.data.models.WeatherUiData
import com.valentinerutto.farmvision.data.network.ApiService

class WeatherRepository(private val apiService: ApiService) {


    suspend fun getWeather(lat: Double, lon: Double): WeatherUiData {
        val weatherResponse = apiService.getWeather(lat, lon)

        return WeatherUiData(
            currentWeather = weatherResponse.toWeatherEntity(),
            dailyWeather = mapToDailyWeatherEntity(weatherResponse)
        )
    }


}
