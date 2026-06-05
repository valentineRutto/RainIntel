package com.valentinerutto.farmvision.data.network.response

data class Hourly(
    val condition_code: String?,
    val feels_like: Double?,
    val humidity: Int?,
    val icon: String?,
    val icon_path: String?,
    val precipitation_probability: Int?,
    val temperature: Double?,
    val time: String?,
    val uv_index: Double?,
    val wind_gust: Double?,
    val wind_speed: Double?
)