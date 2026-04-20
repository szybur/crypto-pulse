package com.pulse.crypto.routes

import com.pulse.crypto.services.RefreshService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.statusRoutes() {
    val refreshService by inject<RefreshService>()

    get("/api/status/sync") {
        call.respond(HttpStatusCode.OK, refreshService.status.value)
    }

    post("/api/refresh") {
        refreshService.refresh()
        call.respond(HttpStatusCode.OK, refreshService.status.value)
    }
}