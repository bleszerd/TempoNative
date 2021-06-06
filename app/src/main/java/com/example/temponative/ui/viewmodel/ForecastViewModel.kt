package com.example.temponative.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.temponative.api.responsedata.Forecast
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.dataholder.DataHolder
import com.example.temponative.retrofit.RetrofitBuilder
import kotlinx.coroutines.*

class ForecastViewModel : ViewModel() {
    var forecastResults = MutableLiveData<ForecastResponseData>()

    fun getCityForecast() {
        try {
            val api = RetrofitBuilder().buildForecast()

            CoroutineScope(Dispatchers.IO).launch {
                val forecastResponse = async { api?.getCityForecast(DataHolder.citySearch) }
                val forecastData = forecastResponse.await()

                withContext(Dispatchers.Main) {
                    if (forecastData?.isSuccessful == true) {
                        forecastResults.value = forecastData.body()
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