package com.valentinerutto.farmvision.data.network.response

data class TreeAnalysisResponse(
    val analysis_id: String?,
    val canopy_coverage_pct: Double?,
    val confidence_score: Double?,
    val county: String?,
    val cv_debug: CvDebug?,
    val farmer_id: String?,
    val land_acres: Double?,
    val location: String?,
    val low_confidence: Boolean?,
    val observations: List<String?>?,
    val original_image_url: String?,
    val overlay_image_url: String?,
    val recommendations: List<String?>?,
    val timestamp: String?,
    val total_tree_count: Int?,
    val tree_density_per_acre: Double?,
    val tree_health: TreeHealth?,
    val tree_species_guess: String?
)