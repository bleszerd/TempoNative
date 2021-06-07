package com.example.temponative.network.api

import com.example.temponative.models.api.ForecastResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://api.hgbrasil.com/"
const val API_KEY = "2ad90ff2"

interface ForecastRequest {
    @GET("weather?key=$API_KEY")
    suspend fun getCityForecast(
        @Query("city_name") city: String?
    ): Response<ForecastResponseData>

    @GET("weather?key=$API_KEY&user_ip=remote")
    suspend fun getCurrentCity(
        @Query("lat") latitude: String?,
        @Query("lon") longitude: String?,
    ): Response<ForecastResponseData>
}