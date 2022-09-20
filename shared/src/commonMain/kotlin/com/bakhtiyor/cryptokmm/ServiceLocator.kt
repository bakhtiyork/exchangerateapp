package com.bakhtiyor.cryptokmm

object ServiceLocator {
    val exchangeRateApi: ExchangeRateAPI by lazy { ExchangeRateAPIImpl() }
}