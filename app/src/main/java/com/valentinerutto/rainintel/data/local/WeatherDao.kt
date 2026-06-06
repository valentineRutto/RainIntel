package com.valentinerutto.rainintel.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_table ORDER BY id DESC LIMIT 1")
    fun observeCurrentLatest(): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather_table ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentLatest(): WeatherEntity?

    @Query("SELECT * FROM daily_table ORDER BY date")
    fun observeDaily(): Flow<List<DailyWeatherEntity>>

    @Query("SELECT * FROM daily_table ORDER BY date")
    suspend fun getDaily(): List<DailyWeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(entity: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDaily(entities: List<DailyWeatherEntity>)

    @Query("DELETE FROM weather_table")
    suspend fun clearCurrent()

    @Query("DELETE FROM daily_table")
    suspend fun clearDaily()

    @Transaction
    suspend fun replaceWeather(
        currentWeather: WeatherEntity,
        dailyWeather: List<DailyWeatherEntity>
    ) {
        clearCurrent()
        clearDaily()
        insertCurrent(currentWeather)
        insertDaily(dailyWeather)
    }
}
