package com.pulse.crypto.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinMarketChartDto(
    @SerialName("prices")
    val prices: List<List<Double>> = emptyList()
)
