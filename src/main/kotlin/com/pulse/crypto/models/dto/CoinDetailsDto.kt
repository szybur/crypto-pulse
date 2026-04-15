package com.pulse.crypto.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailsDto(
    val id: String,
    val symbol: String,
    val name: String,
    val description: DescriptionDto? = null,
    val image: ImageDto? = null,
    val links: LinksDto? = null,
    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null,
    @SerialName("market_data")
    val marketData: MarketDataDto? = null
)

@Serializable
data class DescriptionDto(
    val en: String? = null
)

@Serializable
data class ImageDto(
    val thumb: String? = null,
    val small: String? = null,
    val large: String? = null
)

@Serializable
data class LinksDto(
    val homepage: List<String?> = emptyList()
)

@Serializable
data class MarketDataDto(
    @SerialName("current_price")
    val currentPrice: Map<String, Double>? = null,
    @SerialName("market_cap")
    val marketCap: Map<String, Double>? = null,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = null
)
