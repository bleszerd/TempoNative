package com.example.temponative.models

data class HeaderForecast(
    val date: String,
    val city_name: String,
    val condition_slug: String,
    val temp: Int,
    val wind_speedy: String,
    val sunrise: String,
    val sunset: String,
    val humidity: Int,
)