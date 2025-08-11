package com.example.weather.iteractor

import com.example.weather.api.OpenWeatherApi
import com.example.weather.api.dto.current.toLocationEntity
import com.example.weather.db.dao.LocationDao
import com.example.weather.db.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val weather: Flow<List<LocationEntity>>
    suspend fun fetchWeather(lat: String, lon: String)
}

class LocationRepositoryImpl(
    private val api: OpenWeatherApi,
    private val dao: LocationDao
) : LocationRepository {

    override val weather: Flow<List<LocationEntity>> = dao.getWeather()

    override suspend fun fetchWeather(lat: String, lon: String) {
        val response = api.getConditionsBasedOnCoords(lat = lat, lon = lon)
        val entity = response.toLocationEntity()
        dao.clearWeather()
        dao.insertWeather(entity)
    }
}
