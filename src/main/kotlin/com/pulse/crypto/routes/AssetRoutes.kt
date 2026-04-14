package com.pulse.crypto.routes

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
}
