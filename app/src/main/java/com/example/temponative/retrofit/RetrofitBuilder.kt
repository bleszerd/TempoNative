package com.example.temponative.retrofit

import com.example.temponative.api.requests.BASE_URL
import com.example.temponative.api.requests.ForecastRequest
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