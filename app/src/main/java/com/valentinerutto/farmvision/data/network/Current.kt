package com.valentinerutto.farmvision.data.network

data class Current(
    val condition_code: String?,
    val icon: String?,
    val icon_path: String?,
    val temperature: Double?,
    val time: String?,
    val wind_direction: Int?,
    val wind_speed: Int?
)