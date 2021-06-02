package com.example.temponative.datasource

import com.example.temponative.models.WeekForecast

class WeekForecastDataSource {
    companion object {
        private val forecastList = listOf<WeekForecast>()

        fun createDataset(): List<WeekForecast> {
            return forecastList
        }
    }
}