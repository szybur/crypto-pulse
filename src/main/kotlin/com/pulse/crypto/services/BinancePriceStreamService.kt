package com.pulse.crypto.services

import com.pulse.crypto.clients.BinanceStreamClient
import com.pulse.crypto.clients.topMarketSymbolsLowercase
import com.pulse.crypto.streams.PriceEventBus
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory

class BinancePriceStreamService(
    private val binanceStreamClient: BinanceStreamClient,
    private val priceEventBus: PriceEventBus
) {
    private val logger = LoggerFactory.getLogger(BinancePriceStreamService::class.java)

    suspend fun run() {
        val symbols = topMarketSymbolsLowercase()

        logger.info("Starting Binance price stream for symbols={}", symbols)

        binanceStreamClient.observeTrades(symbols)
            .onEach { event ->
                logger.debug(
                    "Publishing Binance price event assetId={} marketSymbol={} price={}",
                    event.assetId,
                    event.marketSymbol,
                    event.price
                )
            }
            .collect { priceEventBus.emit(it) }
    }
}
