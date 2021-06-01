package com.example.temponative.models

data class WeekForecast(
    var date: String,
    var condition: String,
    var minTemp: String,
    var maxTemp: String
)
