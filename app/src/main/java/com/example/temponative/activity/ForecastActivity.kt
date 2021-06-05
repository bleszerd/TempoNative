package com.example.temponative.activity

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.adapter.WeekForecastAdapter
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.dataholder.DataHolder
import com.example.temponative.datasource.WeekForecastDataSource
import com.example.temponative.models.WeekForecast
import com.example.temponative.retrofit.RetrofitBuilder
import com.example.temponative.utils.Utils
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import retrofit2.Response
import java.lang.Runnable
import java.util.*


class ForecastActivity : AppCompatActivity() {
    var isConnected = false
    private lateinit var weekForecastAdapter: WeekForecastAdapter
    private val utils = Utils()
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var forecastAnimContainer: LinearLayout
    private lateinit var drawerButton: LinearLayout
    private var connectivity: ConnectivityManager? = null
    private var info: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        isConnected = isNetworkAvailable(this)
        forecastAnimContainer = findViewById(R.id.linear_lottie_container)

        if (!isConnected) {
            val handler = Handler()
            val timer = Timer()
            val timerTask = object : TimerTask() {
                override fun run() {
                    handler.post(Runnable {
                        isConnected = isNetworkAvailable(this@ForecastActivity)
                        if (isConnected) {
                            makeSpecificApiRequest()
                            timer.cancel()
                            timer.purge()
                            return@Runnable
                        }

                        Toast.makeText(
                            this@ForecastActivity,
                            "Sem acesso a intenet",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
            timer.schedule(timerTask, 5000, 5000)
        }

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
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }
                R.id.drawer_opt_search -> {
                    val searchNavigationIntent = Intent(this, SearchActivity::class.java)
                    startActivity(searchNavigationIntent)
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }
            }
            true
        }
    }

    override fun onResume() {
        forecastAnimContainer.visibility = View.VISIBLE
        super.onResume()

        makeSpecificApiRequest()
    }

    private fun makeSpecificApiRequest(): Response<ForecastResponseData>? {
        var finalResponse: Response<ForecastResponseData>? = null

        if (isConnected) {
            try {
                val api = RetrofitBuilder().buildForecast()

                CoroutineScope(Dispatchers.IO).launch {
                    val forecastResponse = async { api?.getSpecificForecast(DataHolder.citySearch) }
                    val forecastData = forecastResponse.await()
                    finalResponse = forecastData

                    withContext(Dispatchers.Main) {
                        if (forecastData?.isSuccessful == true) {
                            forecastData.let {
                                weekForecastAdapter.clearAll()

                                for (week in forecastData.body()?.results?.forecast!!) {
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
                                updateUiInfo(forecastData.body())
                            }
                        } else {
                            Toast.makeText(this@ForecastActivity, "Fail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR@", e.message.toString())
            }
        } else {
            Toast.makeText(this@ForecastActivity, "Fail", Toast.LENGTH_SHORT).show()
        }

        return finalResponse
    }

    private suspend fun updateUiInfo(apiResponse: ForecastResponseData?) {
        val dateTextView: TextView = findViewById(R.id.activity_forecast_date_textview)
        val cityTextView: TextView = findViewById(R.id.activity_forecast_city_textview)
        val forecastConditionImageView: ImageView =
            findViewById(R.id.activity_forecast_condition_imageview)
        val forecastTemp: TextView = findViewById(R.id.activity_forecast_temp_textview)
        val headerForecastLinearLayout: LinearLayout = findViewById(R.id.header_forecast)

        dateTextView.text = apiResponse?.results?.date

        cityTextView.text = apiResponse?.results?.city_name
        forecastTemp.text = apiResponse?.results?.temp.toString()
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
        val width = forecastAnimContainer.width

        if (apiResponse?.results?.currently.equals("noite")) {
            headerForecastLinearLayout.setBackgroundResource(R.drawable.night_forecast_gradient_rounded)
        } else {
            headerForecastLinearLayout.setBackgroundResource(R.drawable.day_forecast_gradient_rounded)
        }

        forecastAnimContainer.animate().translationX(-width.toFloat()).start()
        delay(2000)
        forecastAnimContainer.visibility = View.GONE
        forecastAnimContainer.translationX = 0f
    }

    fun isNetworkAvailable(context: Context): Boolean {
        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo

            if (info != null) {
                if (info!!.state == NetworkInfo.State.CONNECTED) {
                    Toast.makeText(context, "CONNECTED", Toast.LENGTH_LONG).show()
                    return true
                }
            } else {
                Toast.makeText(context, "NOT CONNECTED", Toast.LENGTH_LONG).show()
                return false
            }
        }

        return false
    }
}