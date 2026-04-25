package com.pulse.crypto.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BinanceCombinedTradeStreamDto(
    val stream: String,
    val data: BinanceTradeEventDto
)

@Serializable
data class BinanceTradeEventDto(
    @SerialName("e")
    val eventType: String,
    @SerialName("E")
    val eventTime: Long,
    @SerialName("s")
    val symbol: String,
    @SerialName("p")
    val price: String
)
