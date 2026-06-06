package com.valentinerutto.rainintel.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TreeAnalysisDao {

    @Query("SELECT * FROM tree_analyses ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TreeAnalysisEntity>>

    @Query("SELECT * FROM tree_analyses WHERE analysisId = :id")
    suspend fun get(id: String): TreeAnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TreeAnalysisEntity)
}