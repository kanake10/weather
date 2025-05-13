package com.example.weather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.db.entities.CurrentWeatherModel

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM weather WHERE name = :city COLLATE NOCASE")
    suspend fun getWeather(city: String): CurrentWeatherModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(entity: CurrentWeatherModel)
}
