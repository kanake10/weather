package com.example.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.entities.FavoriteCityEntity
import com.example.weather.iteractor.FavoriteInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCitiesViewModel @Inject constructor(
    private val repository: FavoriteInteractor
) : ViewModel() {

    private val _favoriteCities = MutableStateFlow<List<FavoriteCityEntity>>(emptyList())
    val favoriteCities: StateFlow<List<FavoriteCityEntity>> = _favoriteCities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadFavoriteCities()
    }

    fun loadFavoriteCities() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _favoriteCities.value = repository.getAllFavoriteCities()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCityToFavorites(city: FavoriteCityEntity) {
        viewModelScope.launch {
            try {
                repository.addFavoriteCity(city)
                loadFavoriteCities()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteFavoriteCity(city: FavoriteCityEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFavoriteCity(city)
                loadFavoriteCities()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun isCityFavorite(cityName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.isCityFavorite(cityName)
            onResult(result)
        }
    }
}
