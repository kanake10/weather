package com.example.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.db.converters.WeatherTypeConverters
import com.example.weather.db.dao.CurrentWeatherDao
import com.example.weather.db.dao.FavoriteCityDao
import com.example.weather.db.dao.ForecastWeatherDao
import com.example.weather.db.dao.LocationDao
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.db.entities.FavoriteCityEntity
import com.example.weather.db.entities.ForecastModel
import com.example.weather.db.entities.LocationEntity

@Database(entities = [
    CurrentWeatherModel::class,
    ForecastModel::class,
    FavoriteCityEntity::class, LocationEntity::class], version = 7)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val currentWeatherDao: CurrentWeatherDao
    abstract val forecastWeatherDao: ForecastWeatherDao
    abstract val favoriteCityDao : FavoriteCityDao
    abstract val locationDao : LocationDao
}

