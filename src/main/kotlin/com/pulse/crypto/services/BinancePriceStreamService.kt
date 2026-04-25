package com.pulse.crypto.services

import com.pulse.crypto.clients.BinanceStreamClient
import com.pulse.crypto.clients.topMarketSymbolsLowercase
import com.pulse.crypto.streams.PriceEventBus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import org.slf4j.LoggerFactory
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BinancePriceStreamService(
    private val binanceStreamClient: BinanceStreamClient,
    private val priceEventBus: PriceEventBus
) {
    private val logger = LoggerFactory.getLogger(BinancePriceStreamService::class.java)

    suspend fun run() {
        val symbols = topMarketSymbolsLowercase()

        binanceStreamClient.observeTrades(symbols)
            .onStart {
                logger.info("Starting Binance price stream for symbols={}", symbols)
            }
            .onEach { event ->
                logger.debug(
                    "Publishing Binance price event assetId={} marketSymbol={} price={}",
                    event.assetId,
                    event.marketSymbol,
                    event.price
                )
            }
            .onEach { event ->
                priceEventBus.emit(event)
            }
            .retryWhen { cause, attempt ->
                if (cause is CancellationException) {
                    false
                } else {
                    val delayDuration = reconnectDelay(attempt)

                    logger.warn(
                        "Binance stream failed. Reconnecting in {} seconds. attempt={} cause={}",
                        delayDuration.inWholeSeconds,
                        attempt + 1,
                        cause.message
                    )

                    delay(delayDuration)
                    true
                }
            }
            .onCompletion { cause ->
                when (cause) {
                    null -> logger.warn("Binance stream completed normally")
                    is CancellationException -> logger.info("Binance stream cancelled")
                    else -> logger.error("Binance stream completed with error", cause)
                }
            }
            .catch { cause ->
                logger.error("Binance stream stopped permanently", cause)
            }
            .collect()
    }

    private fun reconnectDelay(attempt: Long): Duration {
        val seconds = min(
            a = 60L,
            b = 1L shl attempt.coerceAtMost(6).toInt()
        )

        return seconds.seconds
    }
}
