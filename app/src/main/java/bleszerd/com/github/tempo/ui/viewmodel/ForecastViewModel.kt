package bleszerd.com.github.tempo.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bleszerd.com.github.tempo.models.api.ForecastResponseData
import bleszerd.com.github.tempo.network.retrofit.RetrofitBuilder
import kotlinx.coroutines.*
import retrofit2.Response

class ForecastViewModel : ViewModel() {
    var forecastResults = MutableLiveData<ForecastResponseData?>()
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
                        forecastResults.value = null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", e.message.toString())
        }
    }
}