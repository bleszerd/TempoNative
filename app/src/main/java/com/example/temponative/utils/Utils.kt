package com.example.temponative.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.temponative.R

open class Utils {
    fun handleForecastIcon(condition: String?): Int =
        when (condition) {
            "storm" -> R.drawable.ic_cloud_rain
            "snow" -> R.drawable.ic_snowflake
            "hail" -> R.drawable.ic_snowflake
            "rain" -> R.drawable.ic_cloud_rain
            "fog" -> R.drawable.ic_smog
            "clear_day" -> R.drawable.ic_sunny
            "clear_night" -> R.drawable.ic_nightlight_round
            "cloud" -> R.drawable.ic_cloud
            "cloudly_day" -> R.drawable.ic_cloud_sun
            "cloudly_night" -> R.drawable.ic_cloud_moon
            "none_day" -> R.drawable.ic_day_sunny
            "none_night" -> R.drawable.ic_weather_night
            else -> R.drawable.ic_sun
        }

    fun handleForecastIconColor(context: Context, condition: String?): Int =
        when (condition) {
            "storm" -> ContextCompat.getColor(context, R.color.storm)
            "snow" -> ContextCompat.getColor(context, R.color.storm)
            "hail" -> ContextCompat.getColor(context, R.color.storm)
            "rain" -> ContextCompat.getColor(context, R.color.storm)
            "fog" -> ContextCompat.getColor(context, R.color.fog)
            "clear_day" -> ContextCompat.getColor(context, R.color.sun)
            "clear_night" -> ContextCompat.getColor(context, R.color.night_moon)
            "cloud" -> ContextCompat.getColor(context, R.color.storm)
            "cloudly_day" -> ContextCompat.getColor(context, R.color.sun)
            "cloudly_night" -> ContextCompat.getColor(context, R.color.night_moon)
            "none_day" -> ContextCompat.getColor(context, R.color.sun)
            "none_night" -> ContextCompat.getColor(context, R.color.night_moon)
            else -> R.drawable.ic_sun
        }


}