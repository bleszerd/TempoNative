package com.example.temponative.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.temponative.api.responsedata.Forecast
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.dataholder.DataHolder
import com.example.temponative.retrofit.RetrofitBuilder
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.math.log

class ForecastViewModel : ViewModel() {
    var forecastResults = MutableLiveData<ForecastResponseData>()

    enum class getApiMethod {
        CURRENT,
        BY_NAME
    }

    fun getCityForecast(method: getApiMethod) {
        try {
            val api = RetrofitBuilder().buildForecast()
            var forecastResponse: Response<ForecastResponseData>?

            CoroutineScope(Dispatchers.IO).launch {
                Log.d("method", method.toString())
                if (method == getApiMethod.BY_NAME) {
                    forecastResponse = api?.getCityForecast(DataHolder.citySearch)
                } else {
                    forecastResponse = api?.getCurrentCity(
                        DataHolder.latitude,
                        DataHolder.longitude
                    )
                }
                val forecastData = forecastResponse?.body()
                Log.d("sdsds", forecastResponse.toString())

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