package com.example.weather.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.iteractor.WeatherRepo
import com.example.weather.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State
import com.example.weather.api.dto.forecast.WeatherModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo
) : ViewModel() {

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _currentState = MutableStateFlow(WeatherUiState())
    val currentState: StateFlow<WeatherUiState> = _currentState

    private val _forecastState = MutableStateFlow(WeatherUiState())
    val forecastState: StateFlow<WeatherUiState> = _forecastState

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun searchWeather() {
        viewModelScope.launch {
            val query = _searchQuery.value

            _currentState.value = WeatherUiState(isLoading = true)
            _forecastState.value = WeatherUiState(isLoading = true)

            when (val result = weatherRepo.getCurrentWeatherCondition(query)) {
                is Resource.Success -> {
                    Timber.d("Current weather fetched successfully: ${result.data}")
                    _currentState.value = WeatherUiState(weather = result.data)
                }
                is Resource.Error -> {
                    Timber.e("Error fetching current weather: ${result.message}")
                    _currentState.value = WeatherUiState(errorMessage = result.message)
                }
                else -> Timber.w("Unexpected state in current weather fetch")
            }

            when (val forecastResult = weatherRepo.getFiveDayWeatherCondition(query)) {
                is Resource.Success -> {
                    val rawForecasts = forecastResult.data ?: emptyList()
                    val allItems = rawForecasts.flatMap { it.list }
                    Timber.d("Forecast fetched successfully. Items count: ${allItems.size}")

                    val filteredForecast = filterDailyForecasts(allItems)
                    Timber.d("Filtered forecast count: ${filteredForecast.size}")

                    _forecastState.value = WeatherUiState(forecast = filteredForecast)
                }
                is Resource.Error -> {
                    Timber.e("Error fetching forecast: ${forecastResult.message}")
                    _forecastState.value = WeatherUiState(errorMessage = forecastResult.message)
                }
                else -> Timber.w("Unexpected state in forecast fetch")
            }
        }
    }

    private fun filterDailyForecasts(forecastList: List<WeatherModel>): List<WeatherModel> {
        return forecastList
            .groupBy { it.dt_txt.substring(0, 10) }
            .mapNotNull { (_, dayItems) ->
                dayItems.find { it.dt_txt.contains("12:00:00") }
                    ?: dayItems.getOrNull(dayItems.size / 2)
            }
            .also {
                Timber.d("Filtered to ${it.size} daily forecasts")
            }
    }
}


data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: CurrentWeatherModel? = null,
    val forecast: List<WeatherModel>? = null,
    val errorMessage: String? = null
)
