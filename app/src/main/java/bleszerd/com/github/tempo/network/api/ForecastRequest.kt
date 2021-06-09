package bleszerd.com.github.tempo.network.api

import bleszerd.com.github.tempo.models.api.ForecastResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
const val BASE_URL = "https://api.hgbrasil.com/"
const val API_KEY = "YOUR_API_HERE"

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