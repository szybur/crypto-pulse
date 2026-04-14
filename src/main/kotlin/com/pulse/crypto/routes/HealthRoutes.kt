package com.pulse.crypto.routes

import com.pulse.crypto.services.HealthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.healthRoutes() {
    val healthService by inject<HealthService>()
    get("/health") {
        call.respond(HttpStatusCode.OK, healthService.health())
    }
}
