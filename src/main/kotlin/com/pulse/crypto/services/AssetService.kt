package com.pulse.crypto.services

import com.pulse.crypto.clients.CoinGeckoClient
import com.pulse.crypto.models.domain.AssetDetails
import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.domain.PricePoint

class AssetService(
    private val coinGeckoClient: CoinGeckoClient
) {
    suspend fun getAssets(): List<AssetSummary> {
        return coinGeckoClient.getMarkets()
    }

    suspend fun getAssetDetails(id: String): AssetDetails =
        coinGeckoClient.getAssetDetails(id)

    suspend fun getAssetHistory(id: String, days: Int): List<PricePoint> =
        coinGeckoClient.getAssetHistory(id = id, days = days)
}
