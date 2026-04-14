package com.pulse.crypto.models.mappers

import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.dto.CoinMarketDto

fun CoinMarketDto.toDomain(): AssetSummary =
    AssetSummary(
        id = id,
        symbol = symbol,
        name = name,
        imageUrl = imageUrl,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChangePercentage24h = priceChangePercentage24h
    )
