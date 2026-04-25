package com.pulse.crypto

import com.pulse.crypto.db.DatabaseFactory
import com.pulse.crypto.di.appModule
import com.pulse.crypto.plugins.configureMonitoring
import com.pulse.crypto.plugins.configureSerialization
import com.pulse.crypto.plugins.configureStatusPages
import com.pulse.crypto.routes.configureRoutes
import com.pulse.crypto.services.BinancePriceStreamService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.sse.SSE
import kotlinx.coroutines.launch
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    install(SSE)
    install(Koin) {
        slf4jLogger()
        configureMonitoring()
        configureStatusPages()
        configureSerialization()
        modules(appModule)
    }
    configureRoutes()

    val binancePriceStreamService by inject<BinancePriceStreamService>()
    launch {
        binancePriceStreamService.run()
    }
}
