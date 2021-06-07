package com.example.temponative.api.requests

import com.example.temponative.R
import com.example.temponative.api.responsedata.ForecastResponseData
import com.example.temponative.dataholder.DataHolder
import com.example.temponative.dataholder.DataHolder.Companion.API_KEY
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://api.hgbrasil.com/"

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