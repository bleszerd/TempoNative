package com.example.temponative.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.models.WeekForecast

class WeekForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var weekForecastItems = listOf<WeekForecast>()

    class WeekForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            conditionImageView.setBackgroundResource(R.drawable.ic_sun)
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

    fun updateList(forecasts: List<WeekForecast>) {
        weekForecastItems = forecasts
    }
}