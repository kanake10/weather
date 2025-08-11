package com.example.weather.iteractor

import com.example.weather.db.dao.FavoriteCityDao
import com.example.weather.db.entities.FavoriteCityEntity
import javax.inject.Inject

interface FavoriteInteractor {
    suspend fun addFavoriteCity(city: FavoriteCityEntity)
    suspend fun getAllFavoriteCities(): List<FavoriteCityEntity>
    suspend fun deleteFavoriteCity(city: FavoriteCityEntity)
    suspend fun isCityFavorite(cityName: String): Boolean
}

class FavoriteInteractorImpl @Inject constructor(
    private val dao: FavoriteCityDao
) : FavoriteInteractor {

    override suspend fun addFavoriteCity(city: FavoriteCityEntity) {
        dao.insertFavoriteCity(city)
    }

    override suspend fun getAllFavoriteCities(): List<FavoriteCityEntity> {
        return dao.getAllFavorites()
    }

    override suspend fun deleteFavoriteCity(city: FavoriteCityEntity) {
        dao.deleteFavoriteCity(city)
    }

    override suspend fun isCityFavorite(cityName: String): Boolean {
        return dao.isFavorite(cityName)
    }
}
