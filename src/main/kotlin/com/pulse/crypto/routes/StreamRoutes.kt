package com.pulse.crypto.routes


import com.pulse.crypto.models.domain.PriceUpdateEvent
import com.pulse.crypto.streams.PriceEventBus
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import io.ktor.util.cio.ChannelWriteException
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.ClosedWriteChannelException
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.time.Clock

fun Route.streamRoutes() {
    val priceEventBus by inject<PriceEventBus>()
    val logger = LoggerFactory.getLogger("SseStream")

    sse("/api/events/prices") {
        heartbeat {
            period = 15.seconds
            event = ServerSentEvent(data = "heartbeat")
        }

        try {
            priceEventBus.events.collect { event: PriceUpdateEvent ->
                val json = Json.encodeToString(event)

                send(
                    ServerSentEvent(
                        data = json,
                        event = "price-update"
                    )
                )
            }
        } catch (e: ClosedWriteChannelException) {
            logger.debug("SSE client disconnected")
        } catch (e: ChannelWriteException) {
            logger.debug("SSE channel closed by client")
        } catch (e: CancellationException) {
            logger.debug("SSE stream cancelled")
        }
    }

    get("/api/events/test-publish") {
        val mockPrice = Random.nextDouble(80000.0, 90000.0)

        val event = PriceUpdateEvent(
            assetId = "bitcoin",
            assetSymbol = "btc",
            marketSymbol = "btcusdt",
            price = mockPrice,
            timestamp = Clock.System.now().toString(),
            source = "mock"
        )

        priceEventBus.emit(event)

        call.respond(
            HttpStatusCode.OK,
            event.price.toString()
        )
    }
}
