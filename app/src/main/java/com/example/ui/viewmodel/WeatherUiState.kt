package com.example.ui.viewmodel

import com.example.data.model.LocationInfo
import com.example.data.model.WeatherData
import com.example.data.repository.UserSettings

sealed interface LocationFetchState {
    data object Idle : LocationFetchState
    data object Locating : LocationFetchState
    data class Success(val locationName: String) : LocationFetchState
    data class Error(val message: String) : LocationFetchState
}

data class WeatherUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null,
    val currentLocation: LocationInfo = LocationInfo.INDORE_DEFAULT,
    val userSettings: UserSettings = UserSettings(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<LocationInfo> = emptyList(),
    val searchErrorMessage: String? = null,
    val locationState: LocationFetchState = LocationFetchState.Idle,
    val indiaOverviewWeather: Map<String, WeatherData> = emptyMap(),
    val isIndiaOverviewLoading: Boolean = false,
    val selectedIndiaStateFilter: String? = null
)
