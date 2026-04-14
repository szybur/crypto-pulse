package com.pulse.crypto.clients


import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.dto.CoinMarketDto
import com.pulse.crypto.models.mappers.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

class CoinGeckoClient(
    private val httpClient: HttpClient
) {
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
            throw CoinGeckoException("Failed to fetch markets from CoinGecko", e)
        }

        if (!response.status.isSuccess()) {
            throw CoinGeckoException("CoinGecko returned HTTP ${response.status.value}")
        }

        val dto = try {
            response.body<List<CoinMarketDto>>()
        } catch (e: Exception) {
            throw CoinGeckoException("Failed to parse CoinGecko response", e)
        }

        return dto.map { it.toDomain() }
    }
}

class CoinGeckoException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
