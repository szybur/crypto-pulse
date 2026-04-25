package com.pulse.crypto.clients

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import kotlin.time.Duration.Companion.seconds

fun provideBinanceHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 15.seconds
        }
    }
}
