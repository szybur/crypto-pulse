package com.pulse.crypto.routes

import com.pulse.crypto.clients.exceptions.CoinGeckoException
import com.pulse.crypto.clients.exceptions.CoinNotFoundException
import com.pulse.crypto.services.AssetService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.assetRoutes() {
    val assetService by inject<AssetService>()

    get("/api/assets") {
        call.respond(HttpStatusCode.OK, assetService.getAssets())
    }

    get("/api/assets/{id}") {
        val id = call.parameters["id"]?.trim()

        if (id.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or blank asset id")
            )
            return@get
        }

        try {
            val assetDetails = assetService.getAssetDetails(id)
            call.respond(HttpStatusCode.OK, assetDetails)
        } catch (e: CoinNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Asset '${e.coinId}' not found")
            )
        } catch (e: CoinGeckoException) {
            call.respond(
                HttpStatusCode.BadGateway,
                mapOf("error" to (e.message ?: "Upstream API error"))
            )
        }
    }

    get("/api/assets/{id}/history") {
        val id = call.parameters["id"]?.trim()
        val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 7

        if (id.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or blank asset id")
            )
            return@get
        }

        if (days <= 0) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Query parameter 'days' must be greater than 0")
            )
            return@get
        }

        try {
            val history = assetService.getAssetHistory(id, days)
            call.respond(HttpStatusCode.OK, history)
        } catch (e: CoinNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Asset '${e.coinId}' not found")
            )
        } catch (e: CoinGeckoException) {
            call.respond(
                HttpStatusCode.BadGateway,
                mapOf("error" to (e.message ?: "Upstream API error"))
            )
        }
    }
}
