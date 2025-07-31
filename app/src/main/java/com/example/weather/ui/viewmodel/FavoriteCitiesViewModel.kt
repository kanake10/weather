package com.example.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.db.entities.FavoriteCityEntity
import com.example.weather.iteractor.FavoriteInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCitiesViewModel @Inject constructor(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _favoriteCities = MutableStateFlow<List<FavoriteCityEntity>>(emptyList())
    val favoriteCities: StateFlow<List<FavoriteCityEntity>> = _favoriteCities.asStateFlow()

    private val _isFavoriteMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isFavoriteMap: StateFlow<Map<String, Boolean>> = _isFavoriteMap.asStateFlow()

    init {
        observeFavorites()
    }

     fun observeFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteCities().collect { cities ->
                _favoriteCities.value = cities
                _isFavoriteMap.value = cities.associate { it.name to true }
            }
        }
    }

    fun addFavoriteCity(cityName: String) {
        viewModelScope.launch {
            favoriteInteractor.saveFavoriteCity(FavoriteCityEntity(cityName))
        }
    }

    fun removeFavoriteCity(cityName: String) {
        viewModelScope.launch {
            favoriteInteractor.removeFavoriteCity(FavoriteCityEntity(cityName))
        }
    }

    fun toggleFavoriteCity(cityName: String) {
        viewModelScope.launch {
            favoriteInteractor.isFavoriteCity(FavoriteCityEntity(cityName)).first().let { isFav ->
                if (isFav) {
                    favoriteInteractor.removeFavoriteCity(FavoriteCityEntity(cityName))
                } else {
                    favoriteInteractor.saveFavoriteCity(FavoriteCityEntity(cityName))
                }
            }
        }
    }

    fun isFavoriteCityFlow(cityName: String): Flow<Boolean> {
        return favoriteInteractor.isFavoriteCity(FavoriteCityEntity(cityName))
    }
}