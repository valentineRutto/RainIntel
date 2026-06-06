package com.valentinerutto.rainintel.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT COUNT(*) FROM cities")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<PreloadedCityEntity>)

    @Query(
        """
        SELECT * FROM cities
        WHERE city LIKE '%' || :query || '%' OR country LIKE '%' || :query || '%'
        ORDER BY city
        LIMIT :limit
        """
    )
    suspend fun search(query: String, limit: Int = 20): List<PreloadedCityEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityWeather(city: CityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitiesWeather(cities: List<CityEntity>)

    @Query("SELECT * FROM cities_weather WHERE id = :cityId LIMIT 1")
    suspend fun getCityWeatherById(cityId: Long): CityEntity?

    @Query("SELECT * FROM cities_weather WHERE isSaved = 1 ORDER BY recentSearchTimestamp DESC, city ASC")
    fun getSavedCities(): Flow<List<CityEntity>>

    @Query("UPDATE cities_weather SET isSaved = :isSaved WHERE city = :cityName")
    suspend fun updateSavedStatus(cityName: String, isSaved: Int)

    @Query("UPDATE cities_weather SET isSaved = :isSaved WHERE id = :cityId")
    suspend fun updateSavedStatusById(cityId: Long, isSaved: Int)

    @Query("UPDATE cities_weather SET isRecent = :isRecent, recentSearchTimestamp = :timestamp WHERE id = :cityId")
    suspend fun updateRecentStatusById(cityId: Long, isRecent: Int, timestamp: Long)

    @Query("SELECT * FROM cities_weather WHERE isRecent = 1 ORDER BY recentSearchTimestamp DESC")
    fun observeRecentCityWeather(): Flow<List<CityEntity>>
    @Query("UPDATE cities_weather SET isRecent = 0")
    suspend fun clearRecentSearches()
}
