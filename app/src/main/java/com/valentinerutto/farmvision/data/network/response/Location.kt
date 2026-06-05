package com.valentinerutto.farmvision.data.network.response

data class Location(
    val country: String?,
    val lat: Double?,
    val lon: Double?,
    val requested_lat: Double?,
    val requested_lon: Double?,
    val timezone: String?
)