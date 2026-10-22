package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AlertSeverity
import com.example.data.model.AppThemeMode
import com.example.data.model.IndiaLocationEntry
import com.example.data.model.IndiaLocationsData
import com.example.data.model.LocationInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherAlert
import com.example.data.model.WeatherData
import com.example.data.model.WindSpeedUnit
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.WeatherRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepository(),
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Collect user settings
        viewModelScope.launch {
            preferencesRepository.userSettingsFlow.collect { settings ->
                _uiState.update { current ->
                    current.copy(
                        userSettings = settings,
                        currentLocation = if (current.weatherData == null) settings.lastLocation else current.currentLocation
                    )
                }
                // Initial load if not loaded yet
                if (_uiState.value.weatherData == null) {
                    loadWeather(settings.lastLocation)
                }
            }
        }
    }

    fun loadWeather(location: LocationInfo, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.weatherData == null,
                    isRefreshing = forceRefresh && it.weatherData != null,
                    errorMessage = null,
                    currentLocation = location
                )
            }

            val result = weatherRepository.getWeatherData(location, forceRefresh = forceRefresh)
            result.fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            weatherData = data,
                            errorMessage = null
                        )
                    }
                    preferencesRepository.setLastLocation(location)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.localizedMessage ?: "Failed to fetch weather data. Please check connection."
                        )
                    }
                }
            )
        }
    }

    fun refreshWeather() {
        loadWeather(_uiState.value.currentLocation, forceRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, searchErrorMessage = null) }
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // Debounce
            _uiState.update { it.copy(isSearching = true) }

            val result = weatherRepository.searchLocations(query)
            result.fold(
                onSuccess = { results ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResults = results,
                            searchErrorMessage = if (results.isEmpty()) "No locations found matching \"$query\"" else null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResults = emptyList(),
                            searchErrorMessage = error.localizedMessage ?: "Search failed. Check your internet connection."
                        )
                    }
                }
            )
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false,
                searchErrorMessage = null
            )
        }
    }

    fun selectLocation(location: LocationInfo) {
        clearSearch()
        loadWeather(location)
    }

    fun toggleFavorite(location: LocationInfo) {
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(location)
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            preferencesRepository.setTemperatureUnit(unit)
        }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            preferencesRepository.setWindSpeedUnit(unit)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun clearRecentLocations() {
        viewModelScope.launch {
            preferencesRepository.clearRecentLocations()
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentGpsLocation(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(locationState = LocationFetchState.Locating) }
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val cancellationToken = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val gpsLocation = LocationInfo(
                            name = "Current Location",
                            stateOrRegion = "GPS Coordinates",
                            country = "India",
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timezone = "Asia/Kolkata"
                        )
                        _uiState.update {
                            it.copy(
                                locationState = LocationFetchState.Success("GPS Location detected"),
                                currentLocation = gpsLocation
                            )
                        }
                        loadWeather(gpsLocation, forceRefresh = true)
                    } else {
                        // Fallback to last known location
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                            if (lastLoc != null) {
                                val gpsLocation = LocationInfo(
                                    name = "Current Location",
                                    stateOrRegion = "GPS Coordinates",
                                    country = "India",
                                    latitude = lastLoc.latitude,
                                    longitude = lastLoc.longitude,
                                    timezone = "Asia/Kolkata"
                                )
                                _uiState.update {
                                    it.copy(
                                        locationState = LocationFetchState.Success("GPS Location detected"),
                                        currentLocation = gpsLocation
                                    )
                                }
                                loadWeather(gpsLocation, forceRefresh = true)
                            } else {
                                _uiState.update {
                                    it.copy(locationState = LocationFetchState.Error("Unable to acquire GPS fix. Please ensure location services are enabled."))
                                }
                            }
                        }.addOnFailureListener { e ->
                            _uiState.update {
                                it.copy(locationState = LocationFetchState.Error(e.localizedMessage ?: "Failed to access location"))
                            }
                        }
                    }
                }.addOnFailureListener { e ->
                    _uiState.update {
                        it.copy(locationState = LocationFetchState.Error(e.localizedMessage ?: "Location request failed"))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(locationState = LocationFetchState.Error(e.localizedMessage ?: "Location permission required"))
                }
            }
        }
    }

    fun resetLocationState() {
        _uiState.update { it.copy(locationState = LocationFetchState.Idle) }
    }

    fun setStateFilter(stateName: String?) {
        _uiState.update { it.copy(selectedIndiaStateFilter = stateName) }
    }

    fun loadIndiaHubsWeather() {
        if (_uiState.value.indiaOverviewWeather.isNotEmpty() && !_uiState.value.isIndiaOverviewLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isIndiaOverviewLoading = true) }
            val hubs = IndiaLocationsData.ALL_INDIA_LOCATIONS.filter { it.isMajorHub }
            val map = mutableMapOf<String, WeatherData>()

            for (hub in hubs) {
                val loc = hub.toLocationInfo()
                val res = weatherRepository.getWeatherData(loc, forceRefresh = false)
                res.getOrNull()?.let { data ->
                    map[hub.cityName] = data
                }
            }

            _uiState.update {
                it.copy(
                    indiaOverviewWeather = map,
                    isIndiaOverviewLoading = false
                )
            }
        }
    }

    fun toggleDemoWeatherAlert() {
        _uiState.update { current ->
            val data = current.weatherData ?: return@update current
            val hasAlerts = data.alerts.isNotEmpty()
            val updatedAlerts = if (hasAlerts) {
                emptyList()
            } else {
                listOf(
                    WeatherAlert(
                        title = "Severe Weather Warning",
                        description = "Severe weather warning in effect for ${current.currentLocation.displayName}. Dangerous meteorological conditions detected.",
                        severity = AlertSeverity.WARNING,
                        instruction = "Stay indoors and avoid high-risk low-lying areas. Monitor local weather bulletins.",
                        source = "National Weather Advisory"
                    )
                )
            }
            current.copy(weatherData = data.copy(alerts = updatedAlerts))
        }
    }
}
