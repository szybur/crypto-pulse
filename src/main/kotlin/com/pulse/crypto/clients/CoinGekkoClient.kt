package com.pulse.crypto.clients

import com.pulse.crypto.clients.exceptions.CoinGeckoException
import com.pulse.crypto.clients.exceptions.CoinNotFoundException
import com.pulse.crypto.models.domain.AssetDetails
import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.domain.PricePoint
import com.pulse.crypto.models.dto.CoinDetailsDto
import com.pulse.crypto.models.dto.CoinMarketChartDto
import com.pulse.crypto.models.dto.CoinMarketDto
import com.pulse.crypto.models.mappers.toDomain
import com.pulse.crypto.models.mappers.toPricePoints
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import org.slf4j.LoggerFactory

class CoinGeckoClient(
    private val httpClient: HttpClient
) {
    private val logger = LoggerFactory.getLogger(CoinGeckoClient::class.java)

    suspend fun getMarkets(
        vsCurrency: String = "usd",
        page: Int = 1,
        perPage: Int = 20
    ): List<AssetSummary> {
        val response = try {
            httpClient.get("coins/markets") {
                parameter("vs_currency", vsCurrency)
                parameter("order", "market_cap_desc")
                parameter("per_page", perPage)
                parameter("page", page)
                parameter("sparkline", false)
                parameter("locale", "en")
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch markets from CoinGecko", e)
            throw CoinGeckoException("Failed to fetch markets from CoinGecko", e)
        }

        if (!response.status.isSuccess()) {
            logger.warn("CoinGecko markets call returned status {}", response.status.value)
            throw CoinGeckoException("CoinGecko returned HTTP ${response.status.value}")
        }

        val dto = try {
            response.body<List<CoinMarketDto>>()
        } catch (e: Exception) {
            throw CoinGeckoException("Failed to parse CoinGecko response", e)
        }

        return dto.map { it.toDomain() }
    }

    suspend fun getAssetDetails(id: String): AssetDetails {
        require(id.isNotBlank()) { "Coin id must not be blank" }

        val response = try {
            httpClient.get("coins/$id") {
                parameter("localization", false)
                parameter("tickers", false)
                parameter("market_data", true)
                parameter("community_data", false)
                parameter("developer_data", false)
                parameter("sparkline", false)
            }
        } catch (e: ClientRequestException) {
            logger.error("Client error while fetching asset details for '{}'", id, e)
            if (e.response.status == HttpStatusCode.NotFound) {
                throw CoinNotFoundException(id)
            }
            throw CoinGeckoException("CoinGecko request failed for id '$id'", e)
        } catch (e: Exception) {
            logger.error("Failed to fetch asset details for '{}'", id, e)
            throw CoinGeckoException("Failed to fetch asset details for '$id'", e)
        }

        if (!response.status.isSuccess()) {
            if (response.status == HttpStatusCode.NotFound) {
                throw CoinNotFoundException(id)
            }
            logger.warn("CoinGecko details call for '{}' returned status {}", id, response.status.value)
            throw CoinGeckoException("CoinGecko returned HTTP ${response.status.value} for id '$id'")
        }

        val dto = try {
            response.body<CoinDetailsDto>()
        } catch (e: Exception) {
            throw CoinGeckoException("Failed to parse CoinGecko details response for '$id'", e)
        }

        return dto.toDomain()
    }

    suspend fun getAssetHistory(
        id: String,
        days: Int = 7,
        vsCurrency: String = "usd"
    ): List<PricePoint> {
        require(id.isNotBlank()) { "Coin id must not be blank" }
        require(days > 0) { "Days must be greater than 0" }

        val response = try {
            httpClient.get("coins/$id/market_chart") {
                parameter("vs_currency", vsCurrency)
                parameter("days", days)
                parameter("interval", "hourly")
            }
        } catch (e: ClientRequestException) {
            logger.warn("CoinGecko history call for '{}' retuned client request error {}", id, e.message)
            if (e.response.status == HttpStatusCode.NotFound) {
                throw CoinNotFoundException(id)
            }
            throw CoinGeckoException("CoinGecko request failed for history id '$id'", e)
        } catch (e: Exception) {
            logger.warn("Failed to fetch asset history for '{}'",id)
            throw CoinGeckoException("Failed to fetch asset history for '$id'", e)
        }

        if (!response.status.isSuccess()) {
            logger.warn("CoinGecko history call for '{}' returned status {}", id, response.status.value)
            if (response.status == HttpStatusCode.NotFound) {
                throw CoinNotFoundException(id)
            }
            throw CoinGeckoException("CoinGecko returned HTTP ${response.status.value} for history id '$id'")
        }

        val dto = try {
            response.body<CoinMarketChartDto>()
        } catch (e: Exception) {
            logger.warn("Failed to parse CoinGecko history response for {}", id)
            throw CoinGeckoException("Failed to parse CoinGecko history response for '$id'", e)
        }

        return dto.toPricePoints()
    }
}
