package com.example.temponative.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.component1
import androidx.core.location.component2
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temponative.R
import com.example.temponative.databinding.ActivityForecastBinding
import com.example.temponative.models.api.ForecastResponseData
import com.example.temponative.models.app.WeekForecast
import com.example.temponative.ui.adapter.WeekForecastAdapter
import com.example.temponative.ui.viewmodel.ForecastViewModel
import com.example.temponative.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException
import java.util.*


class ForecastActivity : AppCompatActivity() {
    private lateinit var weekForecastAdapter: WeekForecastAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var forecastAnimContainer: LinearLayout
    private lateinit var drawerButton: LinearLayout
    private lateinit var viewModel: ForecastViewModel
    private lateinit var binding: ActivityForecastBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var connected = verifyConnection()

    //Create a result contract with search activity
    private val resultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extraValue = result.data?.extras?.get("searchExtra").toString()
                if (viewModel.citySearch.value.toString() != extraValue) {
                    handleAnimationStart()
                    viewModel.citySearch.value = extraValue
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set location provider service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        handleGPSPermissions()
        getCurrentLocation()
        populateViewReferences()
        configureObservers()
        configureWeekForecastRecyclerView()
        configureDrawerButton()
        configureDrawer()
    }

    private fun populateViewReferences() {
        forecastAnimContainer = binding.linearLottieContainer
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
    }

    private fun configureObservers() {
        //Configuring observer
        viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)

        //Set observers
        viewModel.forecastResults.observe(this, Observer {
            this.updateUIElements(it)
        })
        viewModel.citySearch.observe(this, Observer { cityName ->
            if (connected) {
                viewModel.getCityForecast(cityName)
            } else {
                Toast.makeText(this@ForecastActivity, "Sem acesso à internet", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.latAndLon.observe(this, Observer { latLon ->
            if (connected) {
                viewModel.getCityForecast(latLon)
            } else {
                Toast.makeText(this@ForecastActivity, "Sem acesso à internet", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun handleDrawerItemSelected() {
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_opt_home -> {
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }
                R.id.drawer_opt_search -> {
                    val searchNavigationIntent = Intent(this, SearchActivity::class.java)
                    resultContract.launch(searchNavigationIntent)
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

        handleDrawerItemSelected()
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
        handleAnimationComplete()
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


    private fun updateUiIcons(
        forecastConditionImageView: ImageView,
        apiResponse: ForecastResponseData?
    ) {
        forecastConditionImageView.setImageResource(
            Utils.handleForecastIcon(apiResponse?.results?.condition_slug)
        )

        forecastConditionImageView.setColorFilter(
            Utils.handleForecastIconColor(
                this,
                apiResponse?.results?.condition_slug
            )
        )
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

    override fun onStart() {
        handleGPSPermissions()

        super.onStart()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val (latitude, longitude) = location
                    viewModel.latAndLon.value = Pair(latitude, longitude)
                }
            }
    }

    private fun handleAnimationComplete() {
        val curtainAnimation = AnimationUtils.loadAnimation(this, R.anim.courtin_close_animation)

        val configureAnimation = {
            curtainAnimation.duration = 600
        }

        val startAnimation = {
            forecastAnimContainer.startAnimation(curtainAnimation)
        }

        configureAnimation()
        startAnimation()

        CoroutineScope(Main).launch {
            delay(500)
            forecastAnimContainer.visibility = View.GONE
            forecastAnimContainer.translationX = 0f
        }

    }

    private fun handleAnimationStart() {
        forecastAnimContainer.visibility = View.VISIBLE
    }

    @Throws(InterruptedException::class, IOException::class)
    fun verifyConnection(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

}