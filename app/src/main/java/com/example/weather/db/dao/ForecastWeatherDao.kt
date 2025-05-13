package com.example.weather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.db.entities.ForecastModel

@Dao
interface ForecastWeatherDao {

    @Query("SELECT * FROM forecast WHERE city = :city COLLATE NOCASE")
    suspend fun getForecast(city: String): ForecastModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(entity: ForecastModel)
}

