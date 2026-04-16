package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class PricePoint(
    val timestamp: Long,
    val price: Double
)
