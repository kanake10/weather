package com.example.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.iteractor.FavoriteInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCitiesViewModel @Inject constructor(
    private val repository: FavoriteInteractor
) : ViewModel() {

    private val _favoriteCities = MutableStateFlow<List<CurrentWeatherModel>>(emptyList())
    val favoriteCities: StateFlow<List<CurrentWeatherModel>> = _favoriteCities.asStateFlow()

    private var recentlyDeletedCity: CurrentWeatherModel? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeFavoriteCities()
    }

    private fun observeFavoriteCities() {
        viewModelScope.launch {
            repository.getAllFavoriteCities()
                .onStart { _isLoading.value = true }
                .catch { e -> _error.value = e.message }
                .collect { cities ->
                    _favoriteCities.value = cities
                    _isLoading.value = false
                }
        }
    }

    fun addCityToFavorites(weather: CurrentWeatherModel) {
        viewModelScope.launch {
            try {
                repository.addFavoriteCity(weather)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteFavoriteCity(weather: CurrentWeatherModel) {
        viewModelScope.launch {
            try {
                recentlyDeletedCity = weather
                repository.deleteFavoriteCity(weather)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun restoreLastDeletedCity() {
        recentlyDeletedCity?.let {
            addCityToFavorites(it)
            recentlyDeletedCity = null
        }
    }
}


