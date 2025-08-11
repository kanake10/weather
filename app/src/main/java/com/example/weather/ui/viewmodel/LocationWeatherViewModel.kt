package com.example.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.entities.LocationEntity
import com.example.weather.iteractor.LocationRepository
import com.example.weather.utils.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationWeatherViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState

    init {
        observeWeather() // Automatically observe DB changes
    }

    private fun observeWeather() {
        viewModelScope.launch {
            locationRepository.weather.collect { weatherList ->
                val weather = weatherList.firstOrNull()
                _weatherState.update {
                    it.copy(
                        isLoading = false,
                        weather = weather,
                    )
                }
            }
        }
    }

    fun fetchLocationAndWeather() {
        _weatherState.update { it.copy(isLoading = true) }
        locationManager.getCurrentLocation { lat, lon ->
            if (lat != null && lon != null) {
                fetchWeather(lat.toString(), lon.toString())
            } else {
                _weatherState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Could not retrieve location"
                    )
                }
            }
        }
    }

    private fun fetchWeather(lat: String, lon: String) {
        viewModelScope.launch {
            try {
                locationRepository.fetchWeather(lat, lon)
                // Weather is updated in DB and will be emitted via `collect`
            } catch (e: Exception) {
                _weatherState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Error fetching weather"
                    )
                }
            }
        }
    }
}

data class WeatherState(
    val isLoading: Boolean = false,
    val weather: LocationEntity? = null,
    val errorMessage: String? = null
)

