package com.example.weather.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.api.dto.forecast.WeatherModel

@Entity(tableName = "forecast")
data class ForecastModel(
    @PrimaryKey val city: String,
    val list: List<WeatherModel>,
    val lastUpdated: Long
)
