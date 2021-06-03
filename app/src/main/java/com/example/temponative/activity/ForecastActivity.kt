package com.example.temponative.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.adapter.WeekForecastAdapter
import com.example.temponative.api.requests.BASE_URL
import com.example.temponative.api.requests.ForecastRequest
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.dataholder.DataHolder
import com.example.temponative.datasource.WeekForecastDataSource
import com.example.temponative.models.WeekForecast
import com.example.temponative.utils.Utils
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ForecastActivity : AppCompatActivity() {
    val dataHolder = DataHolder
    private lateinit var weekForecastAdapter: WeekForecastAdapter
    private val utils = Utils()
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var drawerButton: LinearLayout

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

        drawerButton = findViewById(R.id.drawer_button)

        val data = WeekForecastDataSource.createDataset()
        weekForecastAdapter.updateList(data.toMutableList())

        drawerButton.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_opt_home -> {

                }
                R.id.drawer_opt_search -> {
                    val searchNavigationIntent = Intent(this, SearchActivity::class.java)
                    startActivity(searchNavigationIntent)
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }
            }
            true
        }

        //Do api call and update Ui
        makeApiRequest()

    }

    override fun onResume() {
        super.onResume()

        if (dataHolder.citySearch != null) {
            makeSpecificApiRequest()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    fun makeSpecificApiRequest(): ForecastResponseData? {
        try {
            var resp: ForecastResponseData? = null

            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory((GsonConverterFactory.create()))
                .build()
                .create(ForecastRequest::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                val response = api.getSpecificForecast(dataHolder.citySearch)
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