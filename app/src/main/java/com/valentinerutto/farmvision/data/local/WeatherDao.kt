package com.valentinerutto.farmvision.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

        @Query("SELECT * FROM weather_table WHERE id = 'latest'")
        fun observeLatest(): Flow<WeatherEntity?>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun upsert(entity: WeatherEntity)
    }

