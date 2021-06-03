package com.example.temponative.api.requests

import com.example.temponative.api.responsedata.ForecastResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "2ee6cea8"
const val BASE_URL = "https://api.hgbrasil.com/"

interface ForecastRequest {
    @GET("weather?key=$API_KEY&city_name=Altinopolis,SP")
    suspend fun getForecast(): ForecastResponseData

    @GET("weather?key=$API_KEY")
    suspend fun getSpecificForecast(@Query("city_name") city: String?): ForecastResponseData
//    Call<ForecastResponseData> getSpecificForecast(@Query("city_name") city: String)
//    suspend fun getSpecificForecast(): ForecastResponseData
}