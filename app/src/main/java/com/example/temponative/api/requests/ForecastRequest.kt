package com.example.temponative.api.requests

import com.example.temponative.api.responsedata.ForecastResponseData
import retrofit2.http.GET

const val BASE_URL = "https://api.hgbrasil.com"
const val API_KEY = "2ee6cea8"

interface ForecastRequest {
    @GET("/weather?key=$API_KEY&city_name=Altinopolis,SP")
    suspend fun getForecast(): ForecastResponseData

    @GET("/weather?key=$API_KEY&city_name=Ribeirão Preto,SP")
    suspend fun getRPForecast(): ForecastResponseData
}