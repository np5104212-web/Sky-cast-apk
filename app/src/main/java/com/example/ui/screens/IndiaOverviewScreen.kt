package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IndiaLocationEntry
import com.example.data.model.IndiaLocationsData
import com.example.ui.components.UnitFormatters
import com.example.ui.model.WeatherConditionMapper
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun IndiaOverviewScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    LaunchedEffect(Unit) {
        viewModel.loadIndiaHubsWeather()
    }

    var filterText by remember { mutableStateOf("") }
    val statesList = remember {
        listOf("All States") + IndiaLocationsData.ALL_INDIA_LOCATIONS
            .map { it.stateOrUt }
            .distinct()
            .sorted()
    }

    val selectedState = uiState.selectedIndiaStateFilter

    val filteredLocations = remember(filterText, selectedState) {
        IndiaLocationsData.ALL_INDIA_LOCATIONS.filter { loc ->
            val matchesState = selectedState == null || selectedState == "All States" || loc.stateOrUt.equals(selectedState, ignoreCase = true)
            val matchesSearch = filterText.isBlank() ||
                    loc.cityName.contains(filterText, ignoreCase = true) ||
                    loc.stateOrUt.contains(filterText, ignoreCase = true)
            matchesState && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color(0xFF283851)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("india_overview_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column {
                    Text(
                        text = "India States Weather",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Across all 28 States & 8 Union Territories",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            // Search filter input
            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("india_search_field"),
                placeholder = {
                    Text(
                        "Filter by city or state name (e.g. Indore, Kerala)...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF64B5F6)
                    )
                },
                trailingIcon = {
                    if (filterText.isNotEmpty()) {
                        IconButton(onClick = { filterText = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0x331E3A5F),
                    unfocusedContainerColor = Color(0x221E3A5F),
                    focusedBorderColor = Color(0xFF64B5F6),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = Color(0xFF64B5F6)
                )
            )

            // State filter horizontal chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statesList) { stateName ->
                    val isSelected = (selectedState == null && stateName == "All States") || selectedState == stateName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF2196F3) else Color.White.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF64B5F6) else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                viewModel.setStateFilter(if (stateName == "All States") null else stateName)
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("state_chip_${stateName.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = stateName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Cities List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("india_cities_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLocations) { entry ->
                    val hubWeather = uiState.indiaOverviewWeather[entry.cityName]
                    IndiaCityCard(
                        entry = entry,
                        weatherData = hubWeather,
                        tempUnit = uiState.userSettings.temperatureUnit,
                        onSelect = {
                            viewModel.selectLocation(entry.toLocationInfo())
                            onBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IndiaCityCard(
    entry: IndiaLocationEntry,
    weatherData: com.example.data.model.WeatherData?,
    tempUnit: com.example.data.model.TemperatureUnit,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp)
            .testTag("india_city_${entry.cityName.lowercase().replace(" ", "_")}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E3A5F)),
            contentAlignment = Alignment.Center
        ) {
            if (weatherData != null) {
                val cond = WeatherConditionMapper.getCondition(
                    weatherData.current.weatherCode,
                    weatherData.current.isDay
                )
                Icon(
                    imageVector = cond.icon,
                    contentDescription = cond.title,
                    tint = cond.primaryAccentColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.LocationCity,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.cityName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                if (entry.isMajorHub) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FF9800))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HUB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFFFB74D),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = entry.stateOrUt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            )
        }

        // Live temperature if available
        if (weatherData != null) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = UnitFormatters.formatTemperature(weatherData.current.temperatureC, tempUnit),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = weatherData.current.conditionTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                )
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                contentDescription = "View forecast",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
