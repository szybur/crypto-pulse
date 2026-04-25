package com.pulse.crypto.clients

import com.pulse.crypto.models.domain.PriceUpdateEvent
import com.pulse.crypto.models.dto.BinanceCombinedTradeStreamDto
import com.pulse.crypto.models.mappers.toPriceUpdateEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class BinanceStreamClient(
    private val httpClient: HttpClient
) {
    private val logger = LoggerFactory.getLogger(BinanceStreamClient::class.java)

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun observeTrades(symbols: List<String>): Flow<PriceUpdateEvent> = flow {
        require(symbols.isNotEmpty()) { "At least one symbol is required" }

        val normalizedStreams = symbols
            .map { it.lowercase() }
            .map { "${it}@trade" }

        val path = "/stream?streams=${normalizedStreams.joinToString("/")}"

        logger.info("Connecting to Binance stream path={}", path)

        httpClient.wss(
            host = "stream.binance.com",
            port = 9443,
            path = path
        ) {
            for (frame in incoming) {
                if (frame !is Frame.Text) continue

                val text = frame.readText()

                try {
                    val payload = json.decodeFromString<BinanceCombinedTradeStreamDto>(text)
                    val event = payload.data.toPriceUpdateEvent()

                    if (event != null) {
                        emit(event)
                    } else {
                        logger.debug("Ignoring unsupported Binance market symbol: {}", payload.data.symbol)
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to parse Binance websocket message: {}", text, e)
                }
            }
        }
    }
}