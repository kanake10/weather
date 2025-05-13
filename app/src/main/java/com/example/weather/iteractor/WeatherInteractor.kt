package com.example.weather.iteractor

import com.example.weather.utils.Resource
import com.example.weather.api.OpenWeatherApi
import com.example.weather.api.dto.current.toEntity
import com.example.weather.api.dto.forecast.toEntity
import com.example.weather.db.dao.CurrentWeatherDao
import com.example.weather.db.dao.ForecastWeatherDao
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.db.entities.ForecastModel
import com.example.weather.utils.NetworkHelper
import com.example.weather.utils.safeApiCall
import timber.log.Timber
import javax.inject.Inject

interface WeatherRepo {
    suspend fun getCurrentWeatherCondition(city: String): Resource<CurrentWeatherModel>
    suspend fun getFiveDayWeatherCondition(city: String): Resource<List<ForecastModel>>
}

class WeatherRepoImpl @Inject constructor(
    val openWeatherApi: OpenWeatherApi,
    val currentWeatherDao: CurrentWeatherDao,
    val forecastWeatherDao: ForecastWeatherDao,
    private val networkHelper: NetworkHelper
) : WeatherRepo {

    override suspend fun getCurrentWeatherCondition(city: String): Resource<CurrentWeatherModel> {
        val cached = currentWeatherDao.getWeather(city)
        Timber.d("Cached current weather for $city: $cached")

        return if (networkHelper.isConnected()) {
            Timber.d("Fetching current weather from API for $city")
            val apiResult = safeApiCall {
                val result = openWeatherApi.getCurrentWeather(city)
                val entity = result.toEntity()
                currentWeatherDao.insertWeather(entity)
                Timber.d("Saved current weather to DB for $city: $entity")
                entity
            }

            if (apiResult is Resource.Error) {
                Timber.e("Error fetching current weather: ${apiResult.message}")
            }

            apiResult
        } else {
            cached?.let {
                Timber.d("Returning cached current weather for $city")
                Resource.Success(it)
            } ?: Resource.Error("No internet connection")
        }
    }

    override suspend fun getFiveDayWeatherCondition(city: String): Resource<List<ForecastModel>> {
        val cached = forecastWeatherDao.getForecast(city)
        Timber.d("Cached forecast for $city: $cached")

        return if (networkHelper.isConnected()) {
            Timber.d("Fetching 5-day forecast from API for $city")
            val apiResult = safeApiCall {
                val result = openWeatherApi.getFiveDayForecast(city)
                val entity = result.toEntity()
                forecastWeatherDao.insertForecasts(entity)
                Timber.d("Saved 5-day forecast to DB for $city: $entity")
                listOf(entity)
            }

            if (apiResult is Resource.Error) {
                Timber.e("Error fetching 5-day forecast: ${apiResult.message}")
            }

            apiResult
        } else {
            cached?.let {
                Timber.d("Returning cached forecast for $city")
                Resource.Success(listOf(it))
            } ?: Resource.Error("No internet connection")
        }
    }
}
