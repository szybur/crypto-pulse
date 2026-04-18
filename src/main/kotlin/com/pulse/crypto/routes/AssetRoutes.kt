package com.pulse.crypto.routes

import com.pulse.crypto.clients.exceptions.CoinGeckoException
import com.pulse.crypto.clients.exceptions.CoinNotFoundException
import com.pulse.crypto.services.AssetService
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
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
            ?: throw BadRequestException("Missing asset id")

        val assetDetails = assetService.getAssetDetails(id)
        call.respond(HttpStatusCode.OK, assetDetails)
    }

    get("/api/assets/{id}/history") {
        val id = call.parameters["id"]?.trim()
            ?: throw BadRequestException("Missing asset id")

        val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 7

        require(days > 0) { "Query parameter 'days' must be greater than 0" }

        val history = assetService.getAssetHistory(id, days)
        call.respond(HttpStatusCode.OK, history)
    }
}
