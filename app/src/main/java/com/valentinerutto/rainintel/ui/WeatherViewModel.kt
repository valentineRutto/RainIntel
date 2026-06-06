package com.valentinerutto.rainintel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.rainintel.data.WeatherRepository
import com.valentinerutto.rainintel.data.local.CityEntity
import com.valentinerutto.rainintel.data.local.PreloadedCityEntity
import com.valentinerutto.rainintel.data.models.WeatherUiData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

        viewModelScope.launch {
            repository.observeRecentWeather().collect { recentWeather ->
                _uiSearchState.update {
                    it.copy(recentWeather = recentWeather)
                }
            }
        }

        observeSearch()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _uiSearchState.update {
            it.copy(
                searchQuery = query,
                errorMessage = null
            )
        }


    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiSearchState.update {
                            it.copy(searchResults = emptyList())
                        }
                        return@collectLatest
                    }

                    runCatching {
                        repository.searchPreloadedCities(query)
                    }.onSuccess { cities ->
                        _uiSearchState.update {
                            it.copy(
                                searchResults = cities,
                                errorMessage = null
                            )
                        }
                    }.onFailure { throwable ->
                        _uiSearchState.update {
                            it.copy(
                                searchResults = emptyList(),
                                errorMessage = throwable.message ?: "Unable to search cities"
                            )
                        }
                    }
                }
        }
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

    fun loadWeatherForSelectedCity(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching { repository.refreshWeather(lat, lon) }
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

                    _searchQuery.value = ""
                    _uiSearchState.update {
                        it.copy(
                            isLoading = false,
                            selectedCityWeather = weather,
                            searchQuery = "",
                            searchResults = emptyList(),
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

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
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
    val savedCityWeather: List<CityEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
