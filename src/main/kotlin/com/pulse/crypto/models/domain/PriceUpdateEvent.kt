package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class PriceUpdateEvent(
    val assetId: String,
    val symbol: String,
    val price: Double,
    val timestamp: String,
    val source: String
)
