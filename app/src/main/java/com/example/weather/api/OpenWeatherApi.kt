package com.example.weather.api

import com.example.weather.BuildConfig.API_KEY
import com.example.weather.api.dto.current.WeatherResponse
import com.example.weather.api.dto.forecast.ForecastResponse
import com.example.weather.utils.UNITS
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = UNITS,
    ): WeatherResponse

    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = UNITS,
    ): ForecastResponse

    @GET("weather")
    suspend fun getConditionsBasedOnCoords(
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = UNITS,
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): WeatherResponse
}
