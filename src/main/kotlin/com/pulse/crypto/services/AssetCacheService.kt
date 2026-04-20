package com.pulse.crypto.services

import com.pulse.crypto.models.domain.AssetSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssetCacheService {
    private val _assets = MutableStateFlow<List<AssetSummary>>(emptyList())

    val assets: StateFlow<List<AssetSummary>> = _assets.asStateFlow()

    fun getCurrentAssets(): List<AssetSummary> = _assets.value

    fun updateAssets(newAssets: List<AssetSummary>) {
        _assets.value = newAssets
    }

    fun isEmpty(): Boolean = _assets.value.isEmpty()
}
