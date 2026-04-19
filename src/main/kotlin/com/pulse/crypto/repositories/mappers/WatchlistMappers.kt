package com.pulse.crypto.repositories.mappers

import com.pulse.crypto.db.WatchlistTable
import com.pulse.crypto.models.domain.WatchlistItem
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.time.toKotlinInstant

fun ResultRow.toWatchlistItem(): WatchlistItem =
    WatchlistItem(
        id = this[WatchlistTable.id].value,
        assetId = this[WatchlistTable.assetId],
        symbol = this[WatchlistTable.symbol],
        displayName = this[WatchlistTable.displayName],
        addedAt = this[WatchlistTable.addedAt].toKotlinInstant()
    )