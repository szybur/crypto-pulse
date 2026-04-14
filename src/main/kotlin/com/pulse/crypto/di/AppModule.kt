package com.pulse.crypto.di

import com.pulse.crypto.services.HealthService
import org.koin.dsl.module

val appModule = module {
    single { HealthService() }
}