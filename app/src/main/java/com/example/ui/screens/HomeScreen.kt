package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.LocationInfo
import com.example.ui.components.AnimatedWeatherArt
import com.example.ui.components.DailyForecastPreviewCard
import com.example.ui.components.HourlyForecastRow
import com.example.ui.components.UnitFormatters
import com.example.ui.components.WeatherBackground
import com.example.ui.components.WeatherMetricsGrid
import com.example.ui.model.WeatherCategory
import com.example.ui.model.WeatherConditionMapper
import com.example.ui.viewmodel.LocationFetchState
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onNavigateToSearch: () -> Unit,
    onNavigateToForecast: () -> Unit,
    onNavigateToIndiaOverview: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchCurrentGpsLocation(context)
        } else {
            // Permission denied
        }
    }

    LaunchedEffect(uiState.locationState) {
        when (val state = uiState.locationState) {
            is LocationFetchState.Success -> {
                snackbarHostState.showSnackbar(state.locationName)
                viewModel.resetLocationState()
            }
            is LocationFetchState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetLocationState()
            }
            else -> {}
        }
    }

    val conditionInfo = uiState.weatherData?.let {
        WeatherConditionMapper.getCondition(it.current.weatherCode, it.current.isDay)
    } ?: WeatherConditionMapper.getCondition(0, isDay = true)

    val weatherCategory = conditionInfo.category

    WeatherBackground(
        category = weatherCategory,
        isDarkTheme = false
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Top App Bar
                HomeTopBar(
                    location = uiState.currentLocation,
                    isFavorite = uiState.userSettings.favoriteLocations.any {
                        it.name.equals(uiState.currentLocation.name, ignoreCase = true) &&
                                it.country == uiState.currentLocation.country
                    },
                    isRefreshing = uiState.isRefreshing,
                    onSearchClick = onNavigateToSearch,
                    onGpsClick = {
                        val fineLocationGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        val coarseLocationGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (fineLocationGranted || coarseLocationGranted) {
                            viewModel.fetchCurrentGpsLocation(context)
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    onRefreshClick = { viewModel.refreshWeather() },
                    onFavoriteToggle = { viewModel.toggleFavorite(uiState.currentLocation) },
                    onIndiaOverviewClick = onNavigateToIndiaOverview,
                    onSettingsClick = onNavigateToSettings
                )

                // Main content or loading / error
                if (uiState.isLoading && uiState.weatherData == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Fetching live weather for ${uiState.currentLocation.name}...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                } else if (uiState.errorMessage != null && uiState.weatherData == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = "Error",
                                tint = Color(0xFFFF8A80),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Unable to Load Weather",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { viewModel.refreshWeather() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF0D47A1)
                                ),
                                modifier = Modifier.testTag("retry_button")
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                } else {
                    val weather = uiState.weatherData
                    if (weather != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(bottom = 24.dp)
                        ) {
                            // Hero Temperature & Weather Art
                            WeatherHeroSection(
                                weather = weather,
                                conditionInfo = conditionInfo,
                                tempUnit = uiState.userSettings.temperatureUnit,
                                onCityClick = onNavigateToSearch
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Hourly Forecast Row
                            HourlyForecastRow(
                                hourlyItems = weather.hourly,
                                tempUnit = uiState.userSettings.temperatureUnit,
                                cardBackground = conditionInfo.cardBackground
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Weather Metrics Grid
                            val todayForecast = weather.daily.firstOrNull()
                            WeatherMetricsGrid(
                                current = weather.current,
                                todayForecast = todayForecast,
                                tempUnit = uiState.userSettings.temperatureUnit,
                                windUnit = uiState.userSettings.windSpeedUnit,
                                cardBackground = conditionInfo.cardBackground
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // 7-Day Forecast Preview Card
                            DailyForecastPreviewCard(
                                dailyItems = weather.daily,
                                tempUnit = uiState.userSettings.temperatureUnit,
                                cardBackground = conditionInfo.cardBackground,
                                onViewFullForecast = onNavigateToForecast
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // India Overview Banner Card
                            IndiaOverviewBannerCard(
                                cardBackground = conditionInfo.cardBackground,
                                onClick = onNavigateToIndiaOverview
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Last updated and data attribution footer
                            Text(
                                text = "${weather.current.lastUpdated} • Founded by Naitik • Open-Meteo.com",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    location: LocationInfo,
    isFavorite: Boolean,
    isRefreshing: Boolean,
    onSearchClick: () -> Unit,
    onGpsClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onIndiaOverviewClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refreshAnim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refreshRotate"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App title with subtle weather icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☁",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SkyCast",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        IconButton(
            onClick = onGpsClick,
            modifier = Modifier.testTag("gps_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.MyLocation,
                contentDescription = "Use current GPS location",
                tint = Color.White
            )
        }

        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.testTag("search_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search city",
                tint = Color.White
            )
        }

        IconButton(
            onClick = onRefreshClick,
            modifier = Modifier.testTag("refresh_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Refresh weather",
                tint = Color.White,
                modifier = if (isRefreshing) Modifier.rotate(rotation) else Modifier
            )
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.testTag("settings_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun WeatherHeroSection(
    weather: com.example.data.model.WeatherData,
    conditionInfo: com.example.ui.model.WeatherConditionInfo,
    tempUnit: com.example.data.model.TemperatureUnit,
    onCityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .clickable { onCityClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("location_header_badge"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = weather.location.displayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )
            )
            if (weather.location.country.isNotBlank()) {
                Text(
                    text = ", ${weather.location.country}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Local Time
        Text(
            text = weather.current.localTime,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Center Animated Weather Art
        AnimatedWeatherArt(
            conditionInfo = conditionInfo,
            iconSize = 100.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Large Temperature Display
        Text(
            text = UnitFormatters.formatTemperature(weather.current.temperatureC, tempUnit),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 76.sp,
                letterSpacing = (-2).sp
            ),
            modifier = Modifier.testTag("current_temperature_text")
        )

        // Condition Title
        Text(
            text = weather.current.conditionTitle,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // High / Low & Condition Description
        val today = weather.daily.firstOrNull()
        val highLowText = if (today != null) {
            "H: ${UnitFormatters.formatTemperature(today.maxTempC, tempUnit)}   L: ${UnitFormatters.formatTemperature(today.minTempC, tempUnit)}"
        } else ""

        Text(
            text = if (highLowText.isNotBlank()) "$highLowText  •  ${weather.current.conditionDescription}" else weather.current.conditionDescription,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun IndiaOverviewBannerCard(
    cardBackground: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBackground.copy(alpha = 0.35f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("india_overview_banner"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Map,
                contentDescription = "India Overview",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "India States Weather",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Explore weather across all Indian states and UTs",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = "Navigate to India overview",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
    }
}
