package com.example.temponative.network.retrofit

import com.example.temponative.network.api.BASE_URL
import com.example.temponative.network.api.ForecastRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory((GsonConverterFactory.create()))
            .build()
    }

    fun buildForecast(): ForecastRequest? {
        return buildRetrofit().create(ForecastRequest::class.java)
    }
}