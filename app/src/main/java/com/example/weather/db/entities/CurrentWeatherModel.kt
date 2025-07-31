package com.example.weather.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.api.dto.current.Main
import com.example.weather.api.dto.current.Weather
import com.example.weather.api.dto.current.Wind

@Entity(tableName = "weather")
data class CurrentWeatherModel(
    val weather: List<Weather>,
    val name: String,
    @PrimaryKey val id: Int,
    val wind: Wind,
    val main: Main,
    val lastUpdated: Long
)

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val name: String
)
