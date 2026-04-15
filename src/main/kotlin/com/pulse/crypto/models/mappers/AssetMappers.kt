package com.pulse.crypto.models.mappers

import com.pulse.crypto.models.domain.AssetDetails
import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.dto.CoinDetailsDto
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

fun CoinDetailsDto.toDomain(): AssetDetails =
    AssetDetails(
        id = id,
        symbol = symbol,
        name = name,
        description = description?.en?.takeIf { it.isNotBlank() },
        imageUrl = image?.large ?: image?.small ?: image?.thumb,
        homepage = links?.homepage?.firstOrNull { !it.isNullOrBlank() },
        currentPrice = marketData?.currentPrice?.get("usd"),
        marketCap = marketData?.marketCap?.get("usd")?.toLong(),
        marketCapRank = marketCapRank,
        priceChangePercentage24h = marketData?.priceChangePercentage24h
    )
