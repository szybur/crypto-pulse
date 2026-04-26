package com.pulse.crypto.db

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.timestamp

object WatchlistTable : LongIdTable("watchlist") {
    val assetId = varchar("asset_id", 100).uniqueIndex()
    val symbol = varchar("symbol", 20)
    val displayName = varchar("display_name", 100)
    val addedAt = timestamp("added_at")
}
