package com.valentinerutto.rainintel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.rainintel.data.WeatherRepository
import com.valentinerutto.rainintel.data.local.CityEntity
import com.valentinerutto.rainintel.data.local.PreloadedCityEntity
import com.valentinerutto.rainintel.data.models.WeatherUiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    private val _uiSearchState = MutableStateFlow(WeatherSearchUiState())
    val uiSearchState = _uiSearchState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    init {
        viewModelScope.launch {
            repository.observeWeather().collect { weather ->
                _uiState.update {
                    it.copy(weather = weather)
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun loadWeather(lat: Double, lon: Double) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching { repository.getWeather(lat, lon) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load weather"
                        )
                    }
                }
        }
    }

    fun onCityClicked(city: PreloadedCityEntity) {
        viewModelScope.launch {
            _uiSearchState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching { repository.getWeatherByCity(city) }
                .onSuccess { weather ->

                    _uiSearchState.update {
                        it.copy(
                            isLoading = false,
                            selectedCityWeather = weather,
                            errorMessage = null
                        )
                    }

                    viewModelScope.launch {
                        repository.addToRecentSearches(weather.city)
                    }


                }
                .onFailure { throwable ->
                    _uiSearchState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load weather"
                        )
                    }
                }

        }
    }

}

data class WeatherUiState(
    val weather: WeatherUiData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class WeatherSearchUiState(
    val searchQuery: String = "",
    val searchResults: List<PreloadedCityEntity> = emptyList(),
    val selectedCityWeather: CityEntity? = null,
    val recentWeather: List<CityEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
