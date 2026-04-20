package com.pulse.crypto.routes

import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRoutes() {
    routing {
        healthRoutes()
        assetRoutes()
        watchlistRoutes()
        statusRoutes()
        staticRoutes()
    }
}
