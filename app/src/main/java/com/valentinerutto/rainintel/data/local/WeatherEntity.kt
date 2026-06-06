package com.valentinerutto.rainintel.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.valentinerutto.rainintel.data.network.response.WeatherResponse
import com.valentinerutto.rainintel.util.toDayOfWeek

@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val condition_code: String,
    val icon: String,
    val icon_path: String,
    val temperature: Double,
    val time: String,
    val wind_direction: Double,
    val wind_speed: Double
)

fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    val currentWeather = current
    return WeatherEntity(
        condition_code = currentWeather?.condition_code.orEmpty(),
        icon = currentWeather?.icon.orEmpty(),
        icon_path = currentWeather?.icon_path.orEmpty(),
        temperature = currentWeather?.temperature ?: 0.0,
        time = currentWeather?.time.orEmpty(),
        wind_direction = currentWeather?.wind_direction ?: 0.0,
        wind_speed = currentWeather?.wind_speed ?: 0.0
    )
}


@Entity(tableName = "daily_table")
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val condition_code: String,
    val date: String,
    val icon: String,
    val icon_path: String,
    val precipitation_probability: Int,
    val precipitation_sum: Double,
    val sunrise: String,
    val sunset: String,
    val temp_max: Double,
    val temp_min: Double,
    val wind_max: Double,
    val dayOfTheWeek: String

)

fun mapToDailyWeatherEntity(weatherResponse: WeatherResponse): List<DailyWeatherEntity> {

    return weatherResponse.daily.orEmpty().mapNotNull {

        it?.let { dailyWeather ->
            DailyWeatherEntity(
                condition_code = dailyWeather.condition_code.orEmpty(),
                date = dailyWeather.date.orEmpty(),
                icon = dailyWeather.icon.orEmpty(),
                icon_path = dailyWeather.icon_path.orEmpty(),
                precipitation_probability = dailyWeather.precipitation_probability ?: 0,
                precipitation_sum = dailyWeather.precipitation_sum ?: 0.0,
                sunrise = dailyWeather.sunrise.orEmpty(),
                sunset = dailyWeather.sunset.orEmpty(),
                temp_max = dailyWeather.temp_max ?: 0.0,
                temp_min = dailyWeather.temp_min ?: 0.0,
                wind_max = dailyWeather.wind_max ?: 0.0,
                dayOfTheWeek = dailyWeather.date.orEmpty().toDayOfWeek()
            )
        }
    }

}

