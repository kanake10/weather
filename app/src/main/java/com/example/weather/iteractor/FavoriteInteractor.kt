package com.example.weather.iteractor

import com.example.weather.db.dao.FavoriteCityDao
import com.example.weather.db.entities.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FavoriteInteractor {
    suspend fun saveFavoriteCity(city: FavoriteCityEntity)
    suspend fun removeFavoriteCity(city: FavoriteCityEntity)
    fun getFavoriteCities(): Flow<List<FavoriteCityEntity>>
    fun isFavoriteCity(city: FavoriteCityEntity): Flow<Boolean>
}

class FavoriteInteractorImpl @Inject constructor(
    private val dao: FavoriteCityDao
) : FavoriteInteractor {

    override suspend fun saveFavoriteCity(city: FavoriteCityEntity) {
        dao.insertFavoriteCity(city)
    }

    override suspend fun removeFavoriteCity(city: FavoriteCityEntity) {
        dao.deleteFavoriteCity(city)
    }

    override fun getFavoriteCities(): Flow<List<FavoriteCityEntity>> = dao.getFavoriteCities()

    override fun isFavoriteCity(city: FavoriteCityEntity): Flow<Boolean> {
        return dao.isFavoriteCity(city.name)
    }
}
