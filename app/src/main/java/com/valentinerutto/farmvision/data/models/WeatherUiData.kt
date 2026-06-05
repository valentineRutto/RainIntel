package com.valentinerutto.farmvision.data.models

import com.valentinerutto.farmvision.data.local.DailyWeatherEntity
import com.valentinerutto.farmvision.data.local.WeatherEntity

data class WeatherUiData(
    val currentWeather: WeatherEntity,
    val dailyWeather: List<DailyWeatherEntity>
)
