package com.pulse.crypto.clients.exceptions

class CoinNotFoundException(
    val coinId: String
) : CoinGeckoException("Coin '$coinId' was not found")