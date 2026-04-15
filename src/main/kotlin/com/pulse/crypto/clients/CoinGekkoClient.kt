package com.pulse.crypto.clients


import com.pulse.crypto.clients.exceptions.CoinGeckoException
import com.pulse.crypto.clients.exceptions.CoinNotFoundException
import com.pulse.crypto.models.domain.AssetDetails
import com.pulse.crypto.models.domain.AssetSummary
import com.pulse.crypto.models.dto.CoinDetailsDto
import com.pulse.crypto.models.dto.CoinMarketDto
import com.pulse.crypto.models.mappers.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
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
            if (e.response.status == HttpStatusCode.NotFound) {
                throw CoinNotFoundException(id)
            }
            throw CoinGeckoException("CoinGecko request failed for id '$id'", e)
        } catch (e: Exception) {
            throw CoinGeckoException("Failed to fetch asset details for '$id'", e)
        }

        if (!response.status.isSuccess()) {
            if (response.status == HttpStatusCode.NotFound) {
                throw CoinNotFoundException(id)
            }
            throw CoinGeckoException("CoinGecko returned HTTP ${response.status.value} for id '$id'")
        }

        val dto = try {
            response.body<CoinDetailsDto>()
        } catch (e: Exception) {
            throw CoinGeckoException("Failed to parse CoinGecko details response for '$id'", e)
        }

        return dto.toDomain()
    }
}
