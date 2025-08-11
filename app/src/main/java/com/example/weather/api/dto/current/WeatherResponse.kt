package com.example.weather.api.dto.current

import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.db.entities.FavoriteCityEntity
import com.example.weather.db.entities.LocationEntity

data class WeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val rain: Rain,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

fun WeatherResponse.toEntity(): CurrentWeatherModel {
    return CurrentWeatherModel(
        weather = this.weather,
        name = this.name,
        id = this.id,
        wind = this.wind,
        main = this.main,
        lastUpdated = System.currentTimeMillis()
    )
}

fun WeatherResponse.toLocationEntity(): LocationEntity {
    return LocationEntity(
        weather = this.weather,
        coord =this.coord,
        name = this.name,
        id = this.id,
        wind = this.wind,
        main = this.main,
        lastUpdated = System.currentTimeMillis()
    )
}

fun CurrentWeatherModel.toFavoriteCityEntity(
    timestamp: Long = System.currentTimeMillis()
): FavoriteCityEntity {
    return FavoriteCityEntity(
        id = id,
        name = name.orEmpty(),
        main = main,
        wind = wind,
        weather = weather,
        lastUpdated = timestamp
    )
}

fun FavoriteCityEntity.toCurrentWeatherModel(): CurrentWeatherModel {
    return CurrentWeatherModel(
        id = id,
        name = name,
        main = main,
        wind = wind,
        weather = weather,
        lastUpdated = System.currentTimeMillis()
    )
}
