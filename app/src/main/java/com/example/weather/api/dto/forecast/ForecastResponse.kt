package com.example.weather.api.dto.forecast

import com.example.weather.db.entities.ForecastModel

data class ForecastResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherModel>,
    val message: Int
)

fun ForecastResponse.toEntity(): ForecastModel {
    return ForecastModel(
        city = this.city.name,
        list = this.list,
        lastUpdated = System.currentTimeMillis()
    )
}

