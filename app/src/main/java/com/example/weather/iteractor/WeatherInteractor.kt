package com.example.weather.iteractor

import android.util.Log
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
import jakarta.inject.Inject

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

        return if (networkHelper.isConnected()) {
            val apiResult = safeApiCall {
                val result = openWeatherApi.getCurrentWeather(city)
                val entity = result.toEntity()
                currentWeatherDao.insertWeather(entity)
                entity
            }

            apiResult
        } else {
            cached?.let { Resource.Success(it) }
                ?: Resource.Error("No cached data available and no internet")
        }
    }

    override suspend fun getFiveDayWeatherCondition(city: String): Resource<List<ForecastModel>> {
        val cached = forecastWeatherDao.getForecast(city)
        cached?.let {
            Log.d("WeatherRepoLog", "Loaded cached forecast data: $it")
        }

        if (networkHelper.isConnected()) {
            safeApiCall {
                val result = openWeatherApi.getFiveDayForecast(city)
                val entity = result.toEntity()
                forecastWeatherDao.insertForecasts(entity)
                listOf(entity)
            }
        }

        return cached?.let { Resource.Success(listOf(it)) }
            ?: Resource.Error("No cached forecast data and no internet")
    }

}
