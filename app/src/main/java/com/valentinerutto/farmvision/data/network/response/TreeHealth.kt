package com.valentinerutto.farmvision.data.network.response

data class TreeHealth(
    val healthy: Int?,
    val needs_care: Int?,
    val needs_replacement: Int?
)