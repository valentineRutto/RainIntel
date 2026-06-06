package com.valentinerutto.rainintel.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherEntity::class,DailyWeatherEntity::class,TreeAnalysisEntity::class], version = 1, exportSchema = false)
abstract class RainIntelDatabase: RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun treeAnalysisDao(): TreeAnalysisDao

    companion object Companion {
        @Volatile
        private var INSTANCE: RainIntelDatabase? = null
        fun getDatabase(context: Context): RainIntelDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RainIntelDatabase::class.java,
                    "rainintel_database"
                              ).allowMainThreadQueries()
                .fallbackToDestructiveMigration(false).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
