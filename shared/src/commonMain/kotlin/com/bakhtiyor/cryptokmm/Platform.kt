package com.bakhtiyor.cryptokmm

import io.ktor.client.*

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun createHttpClient(): HttpClient