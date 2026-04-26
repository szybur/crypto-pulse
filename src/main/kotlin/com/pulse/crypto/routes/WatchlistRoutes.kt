package com.pulse.crypto.routes

import com.pulse.crypto.models.requests.AddWatchlistRequest
import com.pulse.crypto.services.WatchlistService
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.watchlistRoutes() {
    val watchlistService by inject<WatchlistService>()

    get("/api/watchlist") {
        val watchlist = watchlistService.getAll()
        call.respond(HttpStatusCode.OK, watchlist)
    }

    post("/api/watchlist") {
        val request = call.receive<AddWatchlistRequest>()

        if (request.assetId.isBlank()) {
            throw BadRequestException("Field 'assetId' must not be blank")
        }

        if (request.symbol.isBlank()) {
            throw BadRequestException("Field 'symbol' must not be blank")
        }

        if (request.displayName.isBlank()) {
            throw BadRequestException("Field 'displayName' must not be blank")
        }

        val created = watchlistService.add(
            assetId = request.assetId,
            symbol = request.symbol,
            displayName = request.displayName
        )

        call.respond(HttpStatusCode.Created, created)
    }

    delete("/api/watchlist/{assetId}") {
        val assetId = call.parameters["assetId"]?.trim()
            ?: throw BadRequestException("Missing assetId")

        val removed = watchlistService.remove(assetId)

        if (!removed) {
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Asset '$assetId' is not on the watchlist")
            )
            return@delete
        }

        call.respond(HttpStatusCode.NoContent, mapOf("message" to "Asset '$assetId' removed from watchlist"))
    }
}
