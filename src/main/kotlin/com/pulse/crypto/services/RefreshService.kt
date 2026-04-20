package com.pulse.crypto.services

import com.pulse.crypto.models.domain.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.slf4j.LoggerFactory
import java.time.Instant

class RefreshService(
    private val assetService: AssetService
) {
    private val logger = LoggerFactory.getLogger(RefreshService::class.java)

    private val _status = MutableStateFlow(
        SyncStatus(
            running = false,
            lastRunAt = null,
            message = "Idle"
        )
    )

    val status: StateFlow<SyncStatus> = _status.asStateFlow()

    suspend fun refresh() {
        logger.info("Refresh started")

        _status.update { current ->
            current.copy(
                running = true,
                message = "Refreshing assets..."
            )
        }

        try {
            val assets = assetService.getAssets()

            _status.value = SyncStatus(
                running = false,
                lastRunAt = Instant.now().toString(),
                message = "Refresh completed successfully. Loaded ${assets.size} assets."
            )

            logger.info("Refresh completed successfully, loaded {} assets", assets.size)
        } catch (e: Exception) {
            logger.error("Refresh failed", e)

            _status.value = SyncStatus(
                running = false,
                lastRunAt = Instant.now().toString(),
                message = "Refresh failed: ${e.message ?: "Unknown error"}"
            )

            throw e
        }
    }
}
