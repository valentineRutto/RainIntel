package com.valentinerutto.rainintel.data.models

import androidx.compose.ui.graphics.Color

data class ForecastDay(
    val day: String,
    val temperature: String,
    val markerColor: Color,
    val selected: Boolean = false,
)