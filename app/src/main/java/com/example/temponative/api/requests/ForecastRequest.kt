package com.example.temponative.api.requests

import com.example.temponative.api.responsedata.ForecastResponseData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "f1e0774f"
const val BASE_URL = "https://api.hgbrasil.com/"

interface ForecastRequest {
    @GET("weather?key=$API_KEY")
    suspend fun getCityForecast(@Query("city_name") city: String?): Response<ForecastResponseData>
}