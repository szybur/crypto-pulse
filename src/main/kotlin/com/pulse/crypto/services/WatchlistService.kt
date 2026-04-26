package com.pulse.crypto.services

import com.pulse.crypto.models.domain.WatchlistItem
import com.pulse.crypto.repositories.WatchlistRepository

class WatchlistService(
    private val watchlistRepository: WatchlistRepository
) {
    suspend fun getAll(): List<WatchlistItem> =
        watchlistRepository.getAll()

    suspend fun add(
        assetId: String,
        symbol: String,
        displayName: String
    ): WatchlistItem =
        watchlistRepository.add(assetId, symbol, displayName)

    suspend fun remove(assetId: String): Boolean =
        watchlistRepository.remove(assetId)

    suspend fun exists(assetId: String): Boolean =
        watchlistRepository.exists(assetId)
}
