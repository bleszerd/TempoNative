package com.example.temponative.datasource

import com.example.temponative.models.WeekForecast

class WeekForecastDataSource {
    companion object {
        fun createDataset(): List<WeekForecast> {
            val forecastList = mutableListOf<WeekForecast>()

            forecastList.add(
                WeekForecast("02/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            forecastList.add(
                WeekForecast("03/06", "condition", "14", "28")
            )

            return forecastList
        }
    }
}