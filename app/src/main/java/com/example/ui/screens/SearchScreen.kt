package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationInfo
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun SearchScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val quickCities = listOf(
        LocationInfo("Indore", "Madhya Pradesh", "India", 22.7196, 75.8577),
        LocationInfo("New Delhi", "Delhi", "India", 28.6139, 77.2090),
        LocationInfo("Mumbai", "Maharashtra", "India", 19.0760, 72.8777),
        LocationInfo("Bengaluru", "Karnataka", "India", 12.9716, 77.5946),
        LocationInfo("Bhopal", "Madhya Pradesh", "India", 23.2599, 77.4126),
        LocationInfo("Gwalior", "Madhya Pradesh", "India", 26.2236, 78.1792),
        LocationInfo("Jaipur", "Rajasthan", "India", 26.9124, 75.7873),
        LocationInfo("Kolkata", "West Bengal", "India", 22.5726, 88.3639),
        LocationInfo("Chennai", "Tamil Nadu", "India", 13.0827, 80.2707),
        LocationInfo("London", null, "United Kingdom", 51.5074, -0.1278),
        LocationInfo("Tokyo", null, "Japan", 35.6762, 139.6503),
        LocationInfo("New York", "New York", "United States", 40.7128, -74.0060)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A192F),
                        Color(0xFF112240),
                        Color(0xFF1E3A5F)
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
                    modifier = Modifier.testTag("search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Search Location",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Search Bar Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_input_field"),
                placeholder = {
                    Text(
                        "Search cities in India or worldwide...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
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
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearSearch() },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear search",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    } else if (uiState.isSearching) {
                        CircularProgressIndicator(
                            color = Color(0xFF64B5F6),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
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

            // Popular Suggestions Quick Chips
            Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                Text(
                    text = "POPULAR LOCATIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.8.sp,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickCities) { city ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    viewModel.selectLocation(city)
                                    onBack()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("quick_city_${city.name.lowercase().replace(" ", "_")}")
                        ) {
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Content / Results
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("search_results_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // If query is active and results are returned
                if (uiState.searchQuery.trim().length >= 2) {
                    if (uiState.searchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "SEARCH RESULTS (${uiState.searchResults.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.6f),
                                    letterSpacing = 0.8.sp
                                ),
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                            )
                        }

                        items(uiState.searchResults) { location ->
                            LocationResultItem(
                                location = location,
                                onSelect = {
                                    viewModel.selectLocation(location)
                                    onBack()
                                }
                            )
                        }
                    } else if (!uiState.isSearching && uiState.searchErrorMessage != null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.searchErrorMessage ?: "No locations found",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Favorites Section
                    val favorites = uiState.userSettings.favoriteLocations
                    if (favorites.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Favorite,
                                    contentDescription = "Favorites",
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FAVORITE CITIES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.6f),
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                        }

                        items(favorites) { fav ->
                            LocationResultItem(
                                location = fav,
                                isFavorite = true,
                                onSelect = {
                                    viewModel.selectLocation(fav)
                                    onBack()
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }

                    // Recent Searches Section
                    val recents = uiState.userSettings.recentLocations
                    if (recents.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.History,
                                        contentDescription = "Recent searches",
                                        tint = Color(0xFF64B5F6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RECENT SEARCHES",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.6f),
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.clearRecentLocations() },
                                    modifier = Modifier.testTag("clear_recents_button")
                                ) {
                                    Text(
                                        text = "Clear",
                                        color = Color(0xFFFF8A80),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        items(recents) { recent ->
                            LocationResultItem(
                                location = recent,
                                onSelect = {
                                    viewModel.selectLocation(recent)
                                    onBack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationResultItem(
    location: LocationInfo,
    isFavorite: Boolean = false,
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
            .testTag("location_item_${location.name.lowercase().replace(" ", "_")}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E3A5F)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.LocationCity,
                contentDescription = null,
                tint = if (isFavorite) Color(0xFFFF8A80) else Color(0xFF64B5F6),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = location.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format(java.util.Locale.US, "%.2f°, %.2f°", location.latitude, location.longitude),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            )
            if (location.country.equals("India", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33FF9800))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "INDIA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFFB74D),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
