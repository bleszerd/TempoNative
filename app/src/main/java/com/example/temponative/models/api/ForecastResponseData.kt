package com.example.temponative.models.api

data class ForecastResponseData(
    val `by`: String,
    val execution_time: Double,
    val from_cache: Boolean,
    val results: Results,
    val valid_key: Boolean
)