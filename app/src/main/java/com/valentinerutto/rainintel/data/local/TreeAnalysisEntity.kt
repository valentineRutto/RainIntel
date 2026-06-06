package com.valentinerutto.rainintel.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tree_analyses")
data class TreeAnalysisEntity(
    @PrimaryKey val analysisId: String,
    val timestamp: String,
    val county: String,
    val location: String,
    val landAcres: Double,
    val totalTreeCount: Int,
    val treeDensityPerAcre: Double,
    val confidenceScore: Double,
    val canopyCoveragePct: Double,
    val healthy: Int,
    val needsCare: Int,
    val needsReplacement: Int,
    val speciesGuess: String,
    val observations: String,
    val recommendations: String,
    val originalImageUrl: String,
    val overlayImageUrl: String,
    val localImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)