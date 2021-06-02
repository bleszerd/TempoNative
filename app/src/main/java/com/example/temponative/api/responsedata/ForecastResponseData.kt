package com.example.temponative.api.responsedata

data class ForecastResponseData(
    val `by`: String,
    val execution_time: Double,
    val from_cache: Boolean,
    val results: Results,
    val valid_key: Boolean
)