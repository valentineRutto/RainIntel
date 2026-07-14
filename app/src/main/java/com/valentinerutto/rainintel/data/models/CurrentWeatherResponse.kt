package com.valentinerutto.rainintel.data.models

import com.google.gson.annotations.SerializedName


data class CurrentWeatherResponse(
    @SerializedName("coord")
    val coordinate: Coordinate? = null,
    @SerializedName("weather")
    val weathers: List<Weather>? = null,
    @SerializedName("main")
    val main: Main? = null
)

data class Coordinate(
    @SerializedName("lon")
    val longitude: Float? = null,
    @SerializedName("lat")
    val latitude: Float? = null
)

data class Weather(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("main")
    val main: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("icon")
    val icon: String? = null
)

data class Main(
    @SerializedName("temp")
    val temperature: Float? = null,
    @SerializedName("temp_max")
    val temperatureMax: Float? = null,
    @SerializedName("temp_min")
    val temperatureMin: Float? = null
)