package com.pulse.crypto.streams

import com.pulse.crypto.models.domain.PriceUpdateEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PriceEventBus {
    private val _events = MutableSharedFlow<PriceUpdateEvent>(
        replay = 0,
        extraBufferCapacity = 64
    )

    val events: SharedFlow<PriceUpdateEvent> = _events.asSharedFlow()

    suspend fun emit(event: PriceUpdateEvent) {
        _events.emit(event)
    }

    fun tryEmit(event: PriceUpdateEvent): Boolean {
        return _events.tryEmit(event)
    }
}
