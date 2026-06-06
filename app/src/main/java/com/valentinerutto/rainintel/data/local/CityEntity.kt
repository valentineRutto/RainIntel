package com.valentinerutto.rainintel.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.valentinerutto.rainintel.data.network.response.WeatherResponse


@Entity(tableName = "cities")
data class PreloadedCityEntity(
    @PrimaryKey val id: Long,
    val city: String,
    val lat: Double,
    val lng: Double,
    val country: String
)

@Entity(tableName = "cities_weather")
data class CityEntity(
    @PrimaryKey
    val id: Long,
    val city: String,
    val lat: Double,
    val lng: Double,
    val country: String,
    val condition_code: String?,
    val icon: String?,
    val icon_path: String?,
    val temperature: Double?,
    val time: String?,
    val isSaved: Boolean = false,
    val isRecent: Boolean = false,
    val recentSearchTimestamp: Long = 0L,)

fun WeatherResponse.toCityEntity(
    selectedCity: PreloadedCityEntity
): CityEntity {
    return CityEntity(
        id = selectedCity.id,
        city = selectedCity.city,
        lat = location?.lat ?: selectedCity.lat,
        lng = location?.lon ?: selectedCity.lng,
        country = location?.country ?: selectedCity.country,
        condition_code = current?.condition_code,
        icon = current?.icon,
        icon_path = current?.icon_path,
        temperature = current?.temperature,
        time = current?.time,
        isSaved = false,
        isRecent = true,
        recentSearchTimestamp = System.currentTimeMillis()
    )
}