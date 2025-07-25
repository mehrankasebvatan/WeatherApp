package ir.kasebvatan.weatherapp.api

import ir.kasebvatan.weatherapp.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") cityName: String
    ): Response<WeatherModel>
}