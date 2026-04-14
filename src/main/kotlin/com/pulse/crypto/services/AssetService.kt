package com.pulse.crypto.services

import com.pulse.crypto.clients.CoinGeckoClient
import com.pulse.crypto.models.domain.AssetSummary

class AssetService(
    private val coinGeckoClient: CoinGeckoClient
) {
    suspend fun getAssets(): List<AssetSummary> {
        return coinGeckoClient.getMarkets()
    }
}
