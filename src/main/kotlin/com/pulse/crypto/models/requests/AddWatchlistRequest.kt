package com.pulse.crypto.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class AddWatchlistRequest(
    val assetId: String,
    val symbol: String,
    val displayName: String
)
