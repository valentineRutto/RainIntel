package com.valentinerutto.rainintel.data.network.response

data class Daily(
    val condition_code: String?,
    val date: String?,
    val icon: String?,
    val icon_path: String?,
    val precipitation_probability: Int?,
    val precipitation_sum: Double?,
    val sunrise: String?,
    val sunset: String?,
    val temp_max: Double?,
    val temp_min: Double?,
    val wind_max: Double?
)