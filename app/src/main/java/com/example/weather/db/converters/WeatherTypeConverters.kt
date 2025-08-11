package com.example.weather.db.converters

import androidx.room.TypeConverter
import com.example.weather.api.dto.current.Coord
import com.example.weather.api.dto.current.Main
import com.example.weather.api.dto.current.Weather
import com.example.weather.api.dto.current.Wind
import com.example.weather.api.dto.forecast.WeatherModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(value: List<Weather>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherList(value: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromWind(wind: Wind): String {
        return gson.toJson(wind)
    }

    @TypeConverter
    fun toWind(value: String): Wind {
        return gson.fromJson(value, Wind::class.java)
    }

    @TypeConverter
    fun fromMain(main: Main): String {
        return gson.toJson(main)
    }

    @TypeConverter
    fun toMain(value: String): Main {
        return gson.fromJson(value, Main::class.java)
    }

    @TypeConverter
    fun fromItemList(list: List<WeatherModel>): String = gson.toJson(list)

    @TypeConverter
    fun toItemList(data: String): List<WeatherModel> {
        val type = object : TypeToken<List<WeatherModel>>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromCoord(coord: Coord): String {
        return gson.toJson(coord)
    }

    @TypeConverter
    fun toCoord(coordString: String): Coord {
        return gson.fromJson(coordString, Coord::class.java)
    }
}
