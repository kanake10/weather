package com.example.weather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.db.entities.FavoriteCityEntity
@Dao
interface FavoriteCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(entity: FavoriteCityEntity)

    @Query("SELECT * FROM favorite ORDER BY lastUpdated DESC")
    suspend fun getAllFavorites(): List<FavoriteCityEntity>

    @Delete
    suspend fun deleteFavoriteCity(entity: FavoriteCityEntity)

    @Query("DELETE FROM favorite")
    suspend fun clearFavorites()

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE name = :city COLLATE NOCASE)")
    suspend fun isFavorite(city: String): Boolean

}
