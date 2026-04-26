package com.pulse.crypto.db

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.slf4j.LoggerFactory
import java.io.File

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init() {
        val dbFile = File("data/cryptopulse.db")
        dbFile.parentFile?.mkdirs()

        val jdbcUrl = "jdbc:sqlite:${dbFile.absolutePath}"

        logger.info("Connecting to SQLite database: {}", jdbcUrl)

        Database.connect(
            url = jdbcUrl,
            driver = "org.sqlite.JDBC"
        )

        transaction {
            logger.info("Creating database schema if needed")
            SchemaUtils.create(WatchlistTable)
        }

        logger.info("Database initialization completed")
    }
}
