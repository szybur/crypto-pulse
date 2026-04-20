package com.pulse.crypto.models.domain

import kotlinx.serialization.Serializable

@Serializable
data class SyncStatus(
    val running: Boolean,
    val lastRunAt: String?,
    val message: String?
)
