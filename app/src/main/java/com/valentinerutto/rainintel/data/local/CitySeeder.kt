package com.valentinerutto.rainintel.data.local

import android.content.Context
import com.opencsv.CSVReaderHeaderAware
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.InputStreamReader

class CitySeeder(
    private val context: Context,
    private val cityDao: CityDao
) {
    private val seedMutex = Mutex()

    suspend fun seedIfNeeded() {
        seedMutex.withLock {
            if (cityDao.count() > 0) return

            context.assets.open(CITIES_ASSET).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    CSVReaderHeaderAware(reader).use { csvReader ->
                        val batch = mutableListOf<PreloadedCityEntity>()
                        var row = csvReader.readMap()

                        while (row != null) {
                            row.toPreloadedCityEntity()?.let { city ->
                                batch += city

                                if (batch.size == BATCH_SIZE) {
                                    cityDao.insertAll(batch.toList())
                                    batch.clear()
                                }
                            }

                            row = csvReader.readMap()
                        }

                        if (batch.isNotEmpty()) {
                            cityDao.insertAll(batch)
                        }
                    }
                }
            }
        }
    }

    private fun Map<String, String>.toPreloadedCityEntity(): PreloadedCityEntity? {
        val id = this["id"]?.toLongOrNull() ?: return null
        val city = this["city_ascii"]?.takeIf { it.isNotBlank() } ?: return null
        val lat = this["lat"]?.toDoubleOrNull() ?: return null
        val lng = this["lng"]?.toDoubleOrNull() ?: return null
        val country = this["country"]?.takeIf { it.isNotBlank() } ?: return null

        return PreloadedCityEntity(
            id = id,
            city = city,
            lat = lat,
            lng = lng,
            country = country
        )
    }

    private companion object {
        const val CITIES_ASSET = "worldcities.csv"
        const val BATCH_SIZE = 500
    }
}
