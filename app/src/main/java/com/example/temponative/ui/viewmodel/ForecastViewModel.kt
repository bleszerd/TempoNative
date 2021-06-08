package com.example.temponative.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.temponative.models.api.ForecastResponseData
import com.example.temponative.network.retrofit.RetrofitBuilder
import kotlinx.coroutines.*
import retrofit2.Response

class ForecastViewModel : ViewModel() {
    var forecastResults = MutableLiveData<ForecastResponseData>()
    var citySearch = MutableLiveData<String>()
    var latAndLon = MutableLiveData<Pair<Double, Double>>()

    fun getCityForecast(cityName: String) {
        try {
            val api = RetrofitBuilder().buildForecast()
            var forecastResponse: Response<ForecastResponseData>?

            CoroutineScope(Dispatchers.IO).launch {
                forecastResponse = api?.getCityForecast(cityName)
                val forecastData = forecastResponse?.body()

                withContext(Dispatchers.Main) {
                    if (forecastResponse?.isSuccessful == true) {
                        forecastResults.value = forecastData
                    } else {
                        Log.e("API_ERROR", "Error!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", e.message.toString())
        }
    }

    fun getCityForecast(latLon: Pair<Double, Double>) {
        try {
            val api = RetrofitBuilder().buildForecast()
            var forecastResponse: Response<ForecastResponseData>?

            CoroutineScope(Dispatchers.IO).launch {
                forecastResponse = api?.getCurrentCity(
                    latLon.first.toString(),
                    latLon.second.toString()
                )

                val forecastData = forecastResponse?.body()

                withContext(Dispatchers.Main) {
                    if (forecastResponse?.isSuccessful == true) {
                        forecastResults.value = forecastData
                    } else {
                        Log.e("API_ERROR", "Error!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", e.message.toString())
        }
    }
}