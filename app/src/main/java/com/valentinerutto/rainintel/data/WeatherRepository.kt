package com.valentinerutto.rainintel.data

import com.valentinerutto.rainintel.data.local.CityDao
import com.valentinerutto.rainintel.data.local.CityEntity
import com.valentinerutto.rainintel.data.local.PreloadedCityEntity
import com.valentinerutto.rainintel.data.local.WeatherDao
import com.valentinerutto.rainintel.data.local.WeatherEntity
import com.valentinerutto.rainintel.data.local.mapToDailyWeatherEntity
import com.valentinerutto.rainintel.data.local.toCityEntity
import com.valentinerutto.rainintel.data.local.toWeatherEntity
import com.valentinerutto.rainintel.data.models.WeatherUiData
import com.valentinerutto.rainintel.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(
    private val apiService: ApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao
) {

    fun observeWeather(): Flow<WeatherUiData?> {
        return combine(
            weatherDao.observeCurrentLatest(),
            weatherDao.observeDaily()
        ) { currentWeather, dailyWeather ->
            currentWeather?.let {
                WeatherUiData(
                    currentWeather = it,
                    dailyWeather = dailyWeather
                )
            }
        }
    }

    suspend fun getWeather(lat: Double, lon: Double) {

        val currentWeather = weatherDao.getCurrentLatest()
        val dailyWeather = weatherDao.getDaily()

        if (currentWeather != null && dailyWeather.isNotEmpty() && currentWeather.isFresh()) {
            return
        }

        refreshWeather(lat, lon)
    }

    suspend fun getWeatherByCity(city: PreloadedCityEntity): CityEntity {
        val weatherResponse = apiService.getWeatherByCity(city.city)

        val selectedCity = weatherResponse.toCityEntity(city)

        weatherDao.replaceWeather(
            currentWeather = weatherResponse.toWeatherEntity(),
            dailyWeather = mapToDailyWeatherEntity(weatherResponse)
        )

        cityDao.insertCityWeather(selectedCity)

        return selectedCity

    }

    suspend fun searchPreloadedCities(query: String): List<PreloadedCityEntity> {
        return if (query.isBlank()) {
            emptyList()
        } else {
            cityDao.search(query.trim())
        }
    }

    suspend fun addToRecentSearches(cityName: String) {
        val timestamp = System.currentTimeMillis()
        cityDao.updateRecentStatus(cityName, 1, timestamp)
    }

    suspend fun clearRecentSearches() {
        cityDao.clearRecentSearches()
    }
    suspend fun removeFromRecentSearches(cityName: String) {
        cityDao.updateRecentStatus(cityName, 0, 0L)
    }

    fun observeRecentWeather(): Flow<List<CityEntity>> {
        return cityDao.observeRecentCityWeather()
    }


    suspend fun refreshWeather(lat: Double, lon: Double) {
        val weatherResponse = apiService.getWeather(lat, lon)
        weatherDao.replaceWeather(
            currentWeather = weatherResponse.toWeatherEntity(),
            dailyWeather = mapToDailyWeatherEntity(weatherResponse)
        )
    }

    private fun WeatherEntity.isFresh(): Boolean {
        val cachedDate = time.substringBefore("T")
        val today = SimpleDateFormat(WEATHER_DATE_PATTERN, Locale.US).format(Date())

        return cachedDate == today
    }

}

private const val WEATHER_DATE_PATTERN = "yyyy-MM-dd"
