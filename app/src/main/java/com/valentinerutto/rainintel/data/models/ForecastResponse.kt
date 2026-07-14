package com.valentinerutto.rainintel.data.models

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list")
    val temperatures: List<Forecast>? = listOf()
)

data class Forecast(
    @SerializedName("dt_txt")
    val timeStampS: String? = null,
    @SerializedName("main")
    val main: Main? = null,
    @SerializedName("weather")
    val weathers: List<Weather>? = null,
    @SerializedName("dt")
    val timeStampL: Long? = null,
)