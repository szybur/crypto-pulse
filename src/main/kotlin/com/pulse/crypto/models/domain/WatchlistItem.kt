package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class WatchlistItem(
    val id: Long,
    val assetId: String,
    val symbol: String,
    val displayName: String,
    val addedAt: Instant
)
