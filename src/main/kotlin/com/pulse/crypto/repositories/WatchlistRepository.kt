package com.pulse.crypto.repositories

import com.pulse.crypto.db.WatchlistTable
import com.pulse.crypto.models.domain.WatchlistItem
import com.pulse.crypto.repositories.exceptions.WatchlistDuplicateException
import com.pulse.crypto.repositories.mappers.toWatchlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant

class WatchlistRepository {

    suspend fun getAll(): List<WatchlistItem> = withContext(Dispatchers.IO) {
        transaction {
            WatchlistTable
                .selectAll()
                .orderBy(WatchlistTable.addedAt)
                .map { it.toWatchlistItem() }
        }
    }

    suspend fun add(
        assetId: String,
        symbol: String,
        displayName: String
    ): WatchlistItem = withContext(Dispatchers.IO) {
        require(assetId.isNotBlank()) { "assetId must not be blank" }
        require(symbol.isNotBlank()) { "symbol must not be blank" }
        require(displayName.isNotBlank()) { "displayName must not be blank" }

        transaction {
            val alreadyExists = WatchlistTable
                .selectAll()
                .where { WatchlistTable.assetId eq assetId }
                .limit(1)
                .count() > 0

            if (alreadyExists) {
                throw WatchlistDuplicateException(assetId)
            }

            val insertedRow = WatchlistTable.insert {
                it[WatchlistTable.assetId] = assetId
                it[WatchlistTable.symbol] = symbol
                it[WatchlistTable.displayName] = displayName
                it[WatchlistTable.addedAt] = Instant.now()
            }

            val insertedId = insertedRow[WatchlistTable.id].value

            WatchlistTable
                .selectAll()
                .where { WatchlistTable.id eq insertedId }
                .single()
                .toWatchlistItem()
        }
    }

    suspend fun remove(assetId: String): Boolean = withContext(Dispatchers.IO) {
        transaction {
            val deletedCount = WatchlistTable.deleteWhere {
                WatchlistTable.assetId eq assetId
            }

            deletedCount > 0
        }
    }

    suspend fun exists(assetId: String): Boolean = withContext(Dispatchers.IO) {
        transaction {
            WatchlistTable
                .selectAll()
                .where { WatchlistTable.assetId eq assetId }
                .limit(1)
                .count() > 0
        }
    }
}
