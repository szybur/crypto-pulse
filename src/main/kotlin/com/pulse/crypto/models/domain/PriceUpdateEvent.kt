package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class PriceUpdateEvent(
    val assetId: String,
    val assetSymbol: String,
    val marketSymbol: String,
    val price: Double,
    val timestamp: String,
    val source: String
)
