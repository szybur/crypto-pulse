package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class AssetSummary(
    val id: String,
    val symbol: String,
    val name: String,
    val imageUrl: String?,
    val currentPrice: Double?,
    val marketCap: Long?,
    val marketCapRank: Int?,
    val priceChangePercentage24h: Double?
)
