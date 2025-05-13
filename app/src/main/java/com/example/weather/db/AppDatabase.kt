package com.example.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.db.converters.WeatherTypeConverters
import com.example.weather.db.dao.CurrentWeatherDao
import com.example.weather.db.dao.ForecastWeatherDao
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.db.entities.ForecastModel

@Database(entities = [CurrentWeatherModel::class, ForecastModel::class], version = 4)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val currentWeatherDao: CurrentWeatherDao
    abstract val forecastWeatherDao: ForecastWeatherDao
}

