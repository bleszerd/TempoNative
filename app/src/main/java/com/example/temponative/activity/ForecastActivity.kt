package com.example.temponative.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.adapter.WeekForecastAdapter
import com.example.temponative.datasource.WeekForecastDataSource

class ForecastActivity : AppCompatActivity() {
    private lateinit var weekForecastAdapter: WeekForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val weekForecastRecyclerView: RecyclerView = findViewById(R.id.forecast_week_recyclerview)
        weekForecastRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@ForecastActivity)
            linearLayoutManager.orientation = (LinearLayoutManager.HORIZONTAL)
            layoutManager = linearLayoutManager
            weekForecastAdapter = WeekForecastAdapter()
            adapter = weekForecastAdapter
        }

        val data = WeekForecastDataSource.createDataset()
        weekForecastAdapter.updateList(data)
    }
}