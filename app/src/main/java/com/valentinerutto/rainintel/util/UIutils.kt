package com.valentinerutto.rainintel.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.valentinerutto.rainintel.data.local.WeatherEntity
import com.valentinerutto.rainintel.data.models.ForecastDay
import com.valentinerutto.rainintel.ui.theme.Mint
import com.valentinerutto.rainintel.ui.theme.RainBlue
import com.valentinerutto.rainintel.ui.theme.SunYellow
import java.time.LocalDate
import java.util.Locale
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun WeatherEntity.updatedTimeLabel(): String {
    return time.toDisplayDateTime()
}



private const val FALLBACK = "--"

private val apiFormatter = DateTimeFormatter.ofPattern(
    API_DATE_TIME_PATTERN,
    Locale.US
)

private val apiDateFormatter = DateTimeFormatter.ofPattern(
    API_DATE_PATTERN,
    Locale.US
)

private val displayDateFormatter = DateTimeFormatter.ofPattern(
    DISPLAY_DATE_TIME_PATTERN,
    Locale.US
)

private val dayOfWeekFormatter = DateTimeFormatter.ofPattern(
    DISPLAY_DAY_OF_WEEK_PATTERN,
    Locale.US
)

private fun String.toLocalDateTimeOrNull(): LocalDateTime? {
    if (isBlank()) return null

    return try {
        LocalDateTime.parse(this, apiFormatter)
    } catch (_: Exception) {
        null
    }
}

private fun String.toLocalDateOrNull(): LocalDate? {
    if (isBlank()) return null

    return toLocalDateTimeOrNull()?.toLocalDate()
        ?: try {
            LocalDate.parse(this, apiDateFormatter)
        } catch (_: Exception) {
            null
        }
}

fun String.toDisplayDateTime(): String {
    val dateTime = toLocalDateTimeOrNull()
        ?: return FALLBACK

    return dateTime.format(displayDateFormatter)
}

fun String.toDayOfWeek(): String {
    val date = toLocalDateOrNull()
        ?: return FALLBACK

    return date.format(dayOfWeekFormatter)
}

private fun String.toDayLabel(): String {
    return takeIf { it.isNotBlank() }
        ?.substringAfterLast("-")
        ?: "--"
}
 fun List<ForecastDay>.withSelectedIndex(selectedIndex: Int): List<ForecastDay> {
    return mapIndexed { index, forecastDay ->
        forecastDay.copy(selected = index == selectedIndex)
    }
}



 fun String.toWeatherIcon(): ImageVector {
    val condition = lowercase()
    return when {
        "rain" in condition || "shower" in condition || "storm" in condition -> Icons.Outlined.Thunderstorm
        "cloud" in condition || "overcast" in condition -> Icons.Outlined.Cloud
        else -> Icons.Outlined.WbSunny
    }
}
fun String.toDisplayCondition(): String {
    if (isBlank()) return "Weather unavailable"

    return lowercase()
        .replace('_', ' ')
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { char -> char.uppercase() }
        }
}

 fun String.toWeatherMarkerColor(): Color {
    val condition = lowercase()
    return when {
        "rain" in condition || "shower" in condition -> RainBlue
        "sun" in condition || "clear" in condition -> SunYellow
        else -> Mint
    }
}
private const val API_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm"
private const val API_DATE_PATTERN = "yyyy-MM-dd"
private const val DISPLAY_DATE_TIME_PATTERN ="yyyy-MM-dd\nHH:mm"

private const val DISPLAY_DAY_OF_WEEK_PATTERN = "EEE"
