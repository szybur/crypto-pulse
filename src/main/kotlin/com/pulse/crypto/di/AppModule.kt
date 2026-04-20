package com.pulse.crypto.di

import com.pulse.crypto.clients.CoinGeckoClient
import com.pulse.crypto.clients.provideCoinGeckoHttpClient
import com.pulse.crypto.repositories.WatchlistRepository
import com.pulse.crypto.services.AssetCacheService
import com.pulse.crypto.services.AssetService
import com.pulse.crypto.services.CoinGeckoAssetService
import com.pulse.crypto.services.HealthService
import com.pulse.crypto.services.RefreshService
import com.pulse.crypto.services.WatchlistService
import org.koin.dsl.module

val appModule = module {
    single { HealthService() }

    single {
        provideCoinGeckoHttpClient(
            apiKey = System.getProperty("COINGECKO_DEMO_API_KEY")
                ?: error("Missing COINGECKO_DEMO_API_KEY")
        )
    }

    single { CoinGeckoClient(get()) }
    single { AssetCacheService() }
    single { CoinGeckoAssetService(get()) }
    single { AssetService(get(), get(), get()) }

    single { WatchlistRepository() }
    single { WatchlistService(get()) }

    single { RefreshService(get(), get()) }
}
