package com.pulse.crypto.plugins

import com.pulse.crypto.clients.exceptions.CoinGeckoException
import com.pulse.crypto.clients.exceptions.CoinNotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatusPages")

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<CoinNotFoundException> { call, cause ->
            logger.warn("Asset not found: {}", cause.coinId)

            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Asset '${cause.coinId}' not found")
            )
        }

        exception<BadRequestException> { call, cause ->
            logger.warn("Bad request: {}", cause.message)

            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to (cause.message ?: "Bad request"))
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            logger.warn("Invalid input: {}", cause.message)

            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to (cause.message ?: "Invalid input"))
            )
        }

        exception<CoinGeckoException> { call, cause ->
            logger.error("CoinGecko error", cause)

            call.respond(
                HttpStatusCode.BadGateway,
                mapOf("error" to (cause.message ?: "Upstream API error"))
            )
        }

        exception<Throwable> { call, cause ->
            logger.error("Unhandled server error", cause)

            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error")
            )
        }
    }
}
