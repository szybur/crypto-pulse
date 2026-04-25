package com.pulse.crypto.models.mappers

import com.pulse.crypto.clients.BinanceAssetMappings
import com.pulse.crypto.models.domain.PriceUpdateEvent
import com.pulse.crypto.models.dto.BinanceTradeEventDto
import java.time.Instant

fun BinanceTradeEventDto.toPriceUpdateEvent(): PriceUpdateEvent? {
    val mapping = BinanceAssetMappings.get(symbol) ?: return null

    return PriceUpdateEvent(
        assetId = mapping.assetId,
        assetSymbol = mapping.assetSymbol,
        marketSymbol = symbol.uppercase(),
        price = price.toDouble(),
        timestamp = Instant.ofEpochMilli(eventTime).toString(),
        source = "binance"
    )
}
