package com.example.temponative.ui.activity

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.adapter.WeekForecastAdapter
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.databinding.ActivityForecastBinding
import com.example.temponative.dataholder.DataHolder
import com.example.temponative.models.WeekForecast
import com.example.temponative.ui.viewmodel.ForecastViewModel
import com.example.temponative.utils.Utils
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import androidx.lifecycle.Observer
import com.example.temponative.ui.viewmodel.ForecastViewModel.getApiMethod.BY_NAME
import com.example.temponative.ui.viewmodel.ForecastViewModel.getApiMethod.CURRENT
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers.Main


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
    private lateinit var viewModel: ForecastViewModel
    private lateinit var binding: ActivityForecastBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        viewModel.forecastResults.observe(this, Observer {
            this.updateUIElements(it)
        })

        forecastAnimContainer = binding.linearLottieContainer
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        isConnected = isNetworkAvailable(this)

        handleConnectionState()
        configureWeekForecastRecyclerView()
        configureDrawerButton()
        configureDrawer()
        handleDrawerItemSelected()
    }

    private fun handleDrawerItemSelected() {
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

    private fun configureDrawer() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureDrawerButton() {
        drawerButton = binding.drawerButton
        drawerButton.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

    private fun configureWeekForecastRecyclerView() {
        val weekForecastRecyclerView: RecyclerView = binding.forecastWeekRecyclerview
        weekForecastRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@ForecastActivity)
            linearLayoutManager.orientation = (LinearLayoutManager.HORIZONTAL)
            layoutManager = linearLayoutManager
            weekForecastAdapter = WeekForecastAdapter()
            adapter = weekForecastAdapter
        }
    }

    private fun handleConnectionState() {
        if (!isConnected) {
            val handler = Handler()
            val timer = Timer()
            val timerTask = object : TimerTask() {
                override fun run() {
                    handler.post(Runnable {
                        isConnected = isNetworkAvailable(this@ForecastActivity)
                        if (isConnected) {
                            getLocaleAndCallForecast()
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
    }

    override fun onResume() {
        handleGPSPermissions()
        forecastAnimContainer.visibility = View.VISIBLE
        if (DataHolder.citySearch.isNotEmpty()) {
            viewModel.getCityForecast(BY_NAME)
        } else {
            getLocaleAndCallForecast()
        }

        Log.d(
            "data latlon",
            DataHolder.latitude + " / " + DataHolder.longitude
        )
        super.onResume()
    }

    private fun updateUIElements(
        forecastData: ForecastResponseData
    ) {
        val newForecastList = mutableListOf<WeekForecast>()

        for (week in forecastData.results.forecast) {
            newForecastList.add(
                WeekForecast(week.date, week.condition, week.min.toString(), week.max.toString())
            )
        }

        weekForecastAdapter.cleanUpdate(newForecastList)
        handleUpdateElements(forecastData)
    }

    private fun handleUpdateElements(apiResponse: ForecastResponseData?) {
        val dateTextView = binding.activityForecastDateTextview
        val cityTextView = binding.activityForecastCityTextview
        val forecastConditionImageView = binding.activityForecastConditionImageview
        val forecastTemp = binding.activityForecastTempTextview
        val headerForecastLinearLayout = binding.headerForecast

        dateTextView.text = apiResponse?.results?.date
        cityTextView.text = apiResponse?.results?.city_name
        forecastTemp.text = apiResponse?.results?.temp.toString()

        updateUiIcons(forecastConditionImageView, apiResponse)
        handleForecastBackgroundGradient(apiResponse, headerForecastLinearLayout)
        CoroutineScope(Main).launch {
            handleCurtainAnim()
        }
    }

    private fun handleForecastBackgroundGradient(
        apiResponse: ForecastResponseData?,
        headerForecastLinearLayout: LinearLayout
    ) {
        if (apiResponse?.results?.currently.equals("noite")) {
            headerForecastLinearLayout.setBackgroundResource(R.drawable.night_forecast_gradient_rounded)
        } else {
            headerForecastLinearLayout.setBackgroundResource(R.drawable.day_forecast_gradient_rounded)
        }
    }

    private suspend fun handleCurtainAnim() {
        val width = forecastAnimContainer.width
        forecastAnimContainer.animate().translationX(-width.toFloat()).start()
        delay(2000)
        forecastAnimContainer.visibility = View.GONE
        forecastAnimContainer.translationX = 0f
    }

    private fun updateUiIcons(
        forecastConditionImageView: ImageView,
        apiResponse: ForecastResponseData?
    ) {
        forecastConditionImageView.setImageResource(
            utils.handleForecastIcon(apiResponse?.results?.condition_slug)
        )

        forecastConditionImageView.setColorFilter(
            utils.handleForecastIconColor(
                this,
                apiResponse?.results?.condition_slug
            )
        )
    }

    fun isNetworkAvailable(context: Context): Boolean {
        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo

            return info != null && info!!.state == NetworkInfo.State.CONNECTED
        }

        return false
    }

    private fun handleGPSPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocaleAndCallForecast() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                DataHolder.latitude = location.latitude.toString()
                DataHolder.longitude = location.longitude.toString()

                viewModel.getCityForecast(CURRENT)

                Log.d(
                    "latlon",
                    location.latitude.toString() + " / " + location.longitude.toString()
                )

                Log.d(
                    "data latlon",
                    DataHolder.latitude + " / " + DataHolder.longitude
                )

            }
    }
}