package com.pulse.crypto.services

import com.pulse.crypto.clients.CoinGeckoClient
import com.pulse.crypto.models.domain.AssetDetails
import com.pulse.crypto.models.domain.AssetScreenData
import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.domain.PricePoint
import com.pulse.crypto.repositories.WatchlistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AssetService(
    private val coinGeckoClient: CoinGeckoClient,
    private val watchlistRepository: WatchlistRepository
) {
    suspend fun getAssets(): List<AssetSummary> {
        return coinGeckoClient.getMarkets()
    }

    suspend fun getAssetDetails(id: String): AssetDetails =
        coinGeckoClient.getAssetDetails(id)

    suspend fun getAssetHistory(id: String, days: Int): List<PricePoint> =
        coinGeckoClient.getAssetHistory(id = id, days = days)

    suspend fun getAssetScreenData(id: String, days: Int = 7): AssetScreenData = coroutineScope {
        val details = async { getAssetDetails(id) }
        val history = async { getAssetHistory(id, days) }
        val watched = async { watchlistRepository.exists(id) }

        AssetScreenData(
            details = details.await(),
            history = history.await(),
            watched = watched.await()
        )
    }
}
