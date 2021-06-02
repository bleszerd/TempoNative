package com.example.temponative.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.datasource.WeekForecastDataSource
import com.example.temponative.models.WeekForecast
import com.example.temponative.utils.Utils

class WeekForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var weekForecastItems = mutableListOf<WeekForecast>()

    class WeekForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val utils = Utils()
        private val dateTextView: TextView = itemView.findViewById(R.id.week_forecast_item_date)
        private val tempMinTextView: TextView =
            itemView.findViewById(R.id.week_forecast_item_min_temp)
        private val tempMaxTextView: TextView =
            itemView.findViewById(R.id.week_forecast_item_max_temp)
        private val conditionImageView: ImageView =
            itemView.findViewById(R.id.week_forecast_item_condition)

        fun bind(weekForecast: WeekForecast) {
            dateTextView.setText(weekForecast.date)
            tempMinTextView.setText(weekForecast.minTemp)
            tempMaxTextView.setText(weekForecast.maxTemp)
            conditionImageView.setImageResource(utils.handleForecastIcon(weekForecast.condition))
            conditionImageView.setColorFilter(
                utils.handleForecastIconColor(
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

    fun updateList(forecasts: MutableList<WeekForecast>) {
        weekForecastItems = forecasts
    }

    fun add(forecast: WeekForecast) {
        Log.d("adding", "Adding: ${forecast.date} for ${forecast.minTemp}")
        weekForecastItems.add(forecast)
    }

    fun clearAll(){
        this.weekForecastItems.clear()
    }

    fun update(forecastsList: MutableList<WeekForecast>) {
        this.weekForecastItems.clear()
        forecastsList.addAll(forecastsList)
    }
}