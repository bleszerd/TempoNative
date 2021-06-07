package com.example.temponative.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.models.app.WeekForecast
import com.example.temponative.utils.Utils

class WeekForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var weekForecastItems = mutableListOf<WeekForecast>()

    class WeekForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.week_forecast_item_date)
        private val tempMinTextView: TextView =
            itemView.findViewById(R.id.week_forecast_item_min_temp)
        private val tempMaxTextView: TextView =
            itemView.findViewById(R.id.week_forecast_item_max_temp)
        private val conditionImageView: ImageView =
            itemView.findViewById(R.id.week_forecast_item_condition)

        fun bind(weekForecast: WeekForecast) {
            dateTextView.text = weekForecast.date
            tempMinTextView.text = weekForecast.minTemp
            tempMaxTextView.text = weekForecast.maxTemp
            handleIcons(weekForecast)
        }

        private fun handleIcons(weekForecast: WeekForecast) {
            conditionImageView.setImageResource(Utils.handleForecastIcon(weekForecast.condition))
            conditionImageView.setColorFilter(
                Utils.handleForecastIconColor(
                    itemView.context,
                    weekForecast.condition
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WeekForecastViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.forecast_week_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WeekForecastViewHolder -> {
                holder.bind(weekForecastItems[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return weekForecastItems.size
    }

    fun add(forecast: WeekForecast) {
        Log.d("adding", "Adding: ${forecast.date} for ${forecast.minTemp}")
        weekForecastItems.add(forecast)
    }

    fun cleanUpdate(forecast: List<WeekForecast>) {
        weekForecastItems.clear()
        weekForecastItems.addAll(forecast)
    }
}