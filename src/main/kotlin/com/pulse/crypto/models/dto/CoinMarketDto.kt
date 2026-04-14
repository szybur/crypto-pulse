package com.pulse.crypto.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinMarketDto(
    val id: String,
    val symbol: String,
    val name: String,
    @SerialName("image")
    val imageUrl: String? = null,
    @SerialName("current_price")
    val currentPrice: Double? = null,
    @SerialName("market_cap")
    val marketCap: Long? = null,
    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = null
)
