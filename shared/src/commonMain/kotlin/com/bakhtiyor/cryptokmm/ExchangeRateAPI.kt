package com.bakhtiyor.cryptokmm

import kotlinx.datetime.LocalDate

interface ExchangeRateAPI {
    suspend fun getSymbols(): List<Symbol>
    suspend fun getRates(symbols: List<String>, base: String = "USD"): List<Rate>
    suspend fun getTimeSeries(start: LocalDate, end: LocalDate, symbol: String, base: String = "USD"): List<TimeSeries>
}