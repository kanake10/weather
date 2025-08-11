package com.example.weather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.db.entities.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(city: FavoriteCityEntity)

    @Query("SELECT * FROM favorite")
    fun getAllFavoritesFlow(): Flow<List<FavoriteCityEntity>>

    @Delete
    suspend fun deleteFavoriteCity(city: FavoriteCityEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE name = :cityName)")
    suspend fun isFavorite(cityName: String): Boolean
}

