package com.valentinerutto.rainintel.data.network.response

data class Current(
    val condition_code: String?,
    val icon: String?,
    val icon_path: String?,
    val temperature: Double?,
    val time: String?,
    val wind_direction: Double?,
    val wind_speed: Double?
)