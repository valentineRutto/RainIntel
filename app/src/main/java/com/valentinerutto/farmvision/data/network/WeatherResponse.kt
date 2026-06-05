package com.valentinerutto.farmvision.data.network

data class WeatherResponse(
    val client_geo: ClientGeo?,
    val current: Current?,
    val daily: List<Daily?>?,
    val hourly: List<Hourly?>?,
    val location: Location?
)