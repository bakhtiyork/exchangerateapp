package com.bakhtiyor.cryptokmm

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExchangeRateHostAPITest {
    val api: ExchangeRateAPI = ServiceLocator.exchangeRateApi

    @Test
    fun testSymbols() = runBlocking {
        val symbols = api.getSymbols()
        assertTrue { symbols.isNotEmpty() }
        assertTrue { symbols.firstOrNull { it.code == "USD" } != null }
    }

    @Test
    fun testRates() = runBlocking {
        val rates = api.getRates(listOf("BTC", "ETH", "DOGE"), base = "USD")
        assertTrue { rates.isNotEmpty() }
        assertTrue { rates.firstOrNull { it.code == "BTC" } != null }
//        assertEquals(3, rates.size)
        println(rates)
    }

    @Test
    fun testTimeSeries() = runBlocking {
        val end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val start = end.minus(14, DateTimeUnit.DAY)
        val timeSeries = api.getTimeSeries(start, end, "USD", base = "BTC")
        assertTrue { timeSeries.isNotEmpty() }
        assertEquals(15, timeSeries.size)
    }
}