package com.bakhtiyor.cryptokmm

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class ExchangeRateAPIImpl : ExchangeRateAPI {

    private val httpClient: HttpClient = createHttpClient()

    override suspend fun getSymbols(): List<Symbol> {
        val httpResponse = httpClient.get("https://api.exchangerate.host/symbols")
        val content = httpResponse.bodyAsText()
        val response = Json.decodeFromString<SymbolsResponse>(content)
        return response.symbols.entries.map { entry ->
            Symbol(code = entry.key, description = entry.value["description"] ?: "")
        }
    }

    override suspend fun getRates(symbols: List<String>, base: String): List<Rate> {
        val symbolsQuery = if (symbols.isNotEmpty()) "&symbols=" + symbols.joinToString(",") else ""
        val url = "https://api.exchangerate.host/latest?base=$base$symbolsQuery"
        val content = httpClient.get(url).bodyAsText()
        val response = Json.decodeFromString<RatesResponse>(content)
        return response.rates.entries.map { entry ->
            Rate(code = entry.key, value = 1.0 / entry.value)
        }
    }

    override suspend fun getTimeSeries(
        start: LocalDate,
        end: LocalDate,
        symbol: String,
        base: String
    ): List<TimeSeries> {
        val url =
            "https://api.exchangerate.host/timeseries?start_date=$start&end_date=$end&symbols=$symbol&base=$base"
        val content = httpClient.get(url).bodyAsText()
        val response = Json.decodeFromString<TimeSeriesResponse>(content)
        return response.rates.entries.map { entry ->
            TimeSeries(
                code = symbol,
                date = entry.key.toLocalDate(),
                value = 1.0 / entry.value.values.first()
            )
        }
    }

}

@Serializable
internal data class SymbolsResponse(
    val motd: Map<String, String>,
    val success: Boolean,
    val symbols: Map<String, Map<String, String>>
)

@Serializable
internal data class RatesResponse(
    val motd: Map<String, String>,
    val success: Boolean,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)

@kotlinx.serialization.Serializable
internal data class TimeSeriesResponse(
    val motd: Map<String, String>,
    val success: Boolean,
    val timeseries: Boolean,
    val base: String,
    val start_date: String,
    val end_date: String,
    val rates: Map<String, Map<String, Double>>
)

