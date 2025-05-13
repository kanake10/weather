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
            _currentState.value = WeatherUiState(isLoading = true)
            _forecastState.value = WeatherUiState(isLoading = true)

            when (val result = weatherRepo.getCurrentWeatherCondition(_searchQuery.value)) {
                is Resource.Success -> {
                    _currentState.value = WeatherUiState(weather = result.data)
                }
                is Resource.Error -> {
                    _currentState.value = WeatherUiState(errorMessage = result.message)
                }
                else -> Unit
            }

            when (val forecastResult = weatherRepo.getFiveDayWeatherCondition(_searchQuery.value)) {
                is Resource.Success -> {
                    val rawForecasts = forecastResult.data ?: emptyList()
                    val allItems = rawForecasts.flatMap { it.list }
                    val filteredForecast = filterDailyForecasts(allItems)
                    _forecastState.value = WeatherUiState(forecast = filteredForecast)
                }
                is Resource.Error -> {
                    _forecastState.value = WeatherUiState(errorMessage = forecastResult.message)
                }
                else -> Unit
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
    }
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: CurrentWeatherModel? = null,
    val forecast: List<WeatherModel>? = null,
    val errorMessage: String? = null
)
