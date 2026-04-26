package com.pulse.crypto.repositories.exceptions

class WatchlistDuplicateException(
    val assetId: String
) : RuntimeException("Asset '$assetId' is already on the watchlist")
