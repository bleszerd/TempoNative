package com.example.temponative.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.adapter.WeekForecastAdapter
import com.example.temponative.api.requests.BASE_URL
import com.example.temponative.api.requests.ForecastRequest
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.datasource.WeekForecastDataSource
import com.example.temponative.models.WeekForecast
import com.example.temponative.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ForecastActivity : AppCompatActivity() {
    private lateinit var weekForecastAdapter: WeekForecastAdapter
    private val utils = Utils()

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
        weekForecastAdapter.updateList(data.toMutableList())

        findViewById<Button>(R.id.rp_button).setOnClickListener {
            makeRPApiRequest()
            Log.d("sdddd", "Do request")
        }

        //Do api call and update Ui
        makeApiRequest()

    }

    fun makeRPApiRequest(): ForecastResponseData? {
        try {
            var resp: ForecastResponseData? = null

            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory((GsonConverterFactory.create()))
                .build()
                .create(ForecastRequest::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                val response = api.getRPForecast()
                resp = response

                weekForecastAdapter.clearAll()

                for (week in response.results.forecast) {
                    weekForecastAdapter.add(
                        WeekForecast(
                            week.date,
                            week.condition,
                            week.min.toString(),
                            week.max.toString()
                        )
                    )
                }

                weekForecastAdapter.notifyDataSetChanged()
                updateUiInfo(response)
            }

            return resp
        } catch (e: Exception) {
            Log.e("ERROR@", e.message.toString())
            return null
        }
    }

    fun makeApiRequest(): ForecastResponseData? {
        try {
            var resp: ForecastResponseData? = null

            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory((GsonConverterFactory.create()))
                .build()
                .create(ForecastRequest::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                val response = api.getForecast()
                resp = response
                for (week in response.results.forecast) {
                    weekForecastAdapter.add(
                        WeekForecast(
                            week.date,
                            week.condition,
                            week.min.toString(),
                            week.max.toString()
                        )
                    )
                }

                weekForecastAdapter.notifyDataSetChanged()
                updateUiInfo(response)
            }

            return resp
        } catch (e: Exception) {
            Log.e("ERROR@", e.message.toString())
            return null
        }
    }

    fun updateUiInfo(apiResponse: ForecastResponseData?) {
        val dateTextView: TextView = findViewById(R.id.activity_forecast_date_textview)
        val cityTextView: TextView = findViewById(R.id.activity_forecast_city_textview)
        val forecastConditionImageView: ImageView =
            findViewById(R.id.activity_forecast_condition_imageview)
        val forecastTemp: TextView = findViewById(R.id.activity_forecast_temp_textview)
        val headerForecastLinearLayout: LinearLayout = findViewById(R.id.header_forecast)

        dateTextView.setText(apiResponse?.results?.date)

        cityTextView.setText(apiResponse?.results?.city_name)
        forecastTemp.setText(apiResponse?.results?.temp.toString())
        forecastConditionImageView.setImageResource(
            utils.handleForecastIcon(apiResponse?.results?.condition_slug)
        )
        forecastConditionImageView.setColorFilter(
            utils.handleForecastIconColor(
                this,
                apiResponse?.results?.condition_slug
            )
        )
        Log.d("noite?", apiResponse?.results?.currently.equals("noite").toString())

        if (apiResponse?.results?.currently.equals("noite")) {
            headerForecastLinearLayout.setBackgroundResource(R.drawable.night_forecast_gradient)
        } else {
            headerForecastLinearLayout.setBackgroundResource(R.drawable.day_forecast_gradient)
        }
    }
}