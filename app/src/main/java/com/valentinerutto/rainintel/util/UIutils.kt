package com.valentinerutto.rainintel.util

import com.valentinerutto.rainintel.data.local.WeatherEntity
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

fun String.toDisplayDateTime(): String {
    val dateTime = toLocalDateTimeOrNull()
        ?: return FALLBACK

    return dateTime.format(displayDateFormatter)
}

fun String.toDayOfWeek(): String {
    val dateTime = toLocalDateTimeOrNull()
        ?: return FALLBACK

    return dateTime.format(dayOfWeekFormatter)
}

private fun String.toDayLabel(): String {
    return takeIf { it.isNotBlank() }
        ?.substringAfterLast("-")
        ?: "--"
}

private const val API_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm"
private const val DISPLAY_DATE_TIME_PATTERN ="yyyy-MM-dd\nHH:mm"

private const val DISPLAY_DAY_OF_WEEK_PATTERN = "EEE"
