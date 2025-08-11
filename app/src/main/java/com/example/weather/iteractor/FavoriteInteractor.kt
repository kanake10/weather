package com.example.weather.iteractor

import com.example.weather.api.dto.current.toCurrentWeatherModel
import com.example.weather.api.dto.current.toFavoriteCityEntity
import com.example.weather.db.dao.FavoriteCityDao
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.db.entities.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface FavoriteInteractor {
    suspend fun addFavoriteCity(weather: CurrentWeatherModel)
    fun getAllFavoriteCities(): Flow<List<CurrentWeatherModel>>
    suspend fun deleteFavoriteCity(weather: CurrentWeatherModel)
    suspend fun isCityFavorite(cityName: String): Boolean
}



class FavoriteInteractorImpl @Inject constructor(
    private val dao: FavoriteCityDao
) : FavoriteInteractor {

    override suspend fun addFavoriteCity(weather: CurrentWeatherModel) {
        dao.insertFavoriteCity(weather.toFavoriteCityEntity())
    }

    override fun getAllFavoriteCities(): Flow<List<CurrentWeatherModel>> {
        return dao.getAllFavoritesFlow().map { list ->
            list.map { it.toCurrentWeatherModel() }
        }
    }

    override suspend fun deleteFavoriteCity(weather: CurrentWeatherModel) {
        dao.deleteFavoriteCity(weather.toFavoriteCityEntity())
    }

    override suspend fun isCityFavorite(cityName: String): Boolean {
        return dao.isFavorite(cityName)
    }
}
