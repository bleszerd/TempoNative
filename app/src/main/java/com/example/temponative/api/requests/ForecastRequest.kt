package com.example.temponative.api.requests

import com.example.temponative.api.responsedata.ForecastResponseData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "d11aa521"
const val BASE_URL = "https://api.hgbrasil.com/"

interface ForecastRequest {
    @GET("weather?key=$API_KEY&city_name=Altinopolis,SP")
    suspend fun getForecast(): Response<ForecastResponseData>

    @GET("weather?key=$API_KEY")
    suspend fun getSpecificForecast(@Query("city_name") city: String?): Response<ForecastResponseData>
}