package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class AssetScreenData(
    val details: AssetDetails,
    val history: List<PricePoint>,
    val watched: Boolean
)
