package com.pulse.crypto.clients

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun provideCoinGeckoHttpClient(apiKey: String): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = false
                    isLenient = true
                }
            )
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpRequestRetry) {
            maxRetries = 2

            retryIf { _, response ->
                response.status.value >= 500
            }

            retryOnExceptionIf { _, cause ->
                cause is java.net.SocketTimeoutException ||
                        cause is io.ktor.client.network.sockets.ConnectTimeoutException ||
                        cause is java.io.IOException
            }

            exponentialDelay()
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10.seconds.inWholeMilliseconds
            connectTimeoutMillis = 5.seconds.inWholeMilliseconds
            socketTimeoutMillis = 10.seconds.inWholeMilliseconds
        }

        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.coingecko.com"
                encodedPath = "/api/v3/"
            }
            header("x-cg-demo-api-key", apiKey)
        }
    }
}