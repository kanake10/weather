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

    @Delete
    suspend fun deleteFavoriteCity(city: FavoriteCityEntity)

    @Query("SELECT * FROM favorite_cities")
    fun getFavoriteCities(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE name = :cityName)")
    fun isFavoriteCity(cityName: String): Flow<Boolean>
}
