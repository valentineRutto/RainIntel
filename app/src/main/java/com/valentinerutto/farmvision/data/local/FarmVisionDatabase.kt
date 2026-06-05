package com.valentinerutto.farmvision.data.local

import androidx.room.Database

@Database(entities = [], version = 1, exportSchema = true)
abstract class FarmVisionDatabase {
    abstract fun farmVisionDao(): FarmVisionDao
}

