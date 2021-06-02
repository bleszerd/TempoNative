package com.example.temponative.api.requests

import com.example.temponative.api.responsedata.ForecastResponseData
import retrofit2.http.GET

const val BASE_URL = "https://api.hgbrasil.com"

interface ForecastRequest {
    @GET("/weather?key=45c41da6&city_name=Altinopolis,SP")
    suspend fun getForecast(): ForecastResponseData

    @GET("/weather?key=45c41da6&city_name=Ribeirão Preto,SP")
    suspend fun getRPForecast(): ForecastResponseData
}