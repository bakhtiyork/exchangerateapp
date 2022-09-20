package com.bakhtiyor.cryptokmm

import kotlinx.datetime.LocalDate

data class Symbol(
    val code: String,
    val description: String
)

data class Rate(
    val code: String,
    val value: Double
)

data class TimeSeries(
    val code: String,
    val date: LocalDate,
    val value: Double
)