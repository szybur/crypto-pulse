package com.pulse.crypto.clients.exceptions

open class CoinGeckoException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
