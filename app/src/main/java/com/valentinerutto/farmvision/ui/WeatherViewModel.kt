package com.valentinerutto.farmvision.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.farmvision.data.WeatherRepository
import com.valentinerutto.farmvision.data.models.WeatherUiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel()  {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeWeather().collect { weather ->
                _uiState.update {
                    it.copy(weather = weather)
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


}

data class WeatherUiState(
    val weather: WeatherUiData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
