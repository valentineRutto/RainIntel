package com.valentinerutto.rainintel.data.models

import com.valentinerutto.rainintel.data.local.DailyWeatherEntity
import com.valentinerutto.rainintel.data.local.WeatherEntity

data class WeatherUiData(
    val currentWeather: WeatherEntity,
    val dailyWeather: List<DailyWeatherEntity>
)
