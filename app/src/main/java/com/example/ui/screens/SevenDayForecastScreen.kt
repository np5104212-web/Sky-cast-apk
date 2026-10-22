package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecastItem
import com.example.data.model.TemperatureUnit
import com.example.ui.components.UnitFormatters
import com.example.ui.model.WeatherConditionMapper
import com.example.ui.viewmodel.WeatherUiState

@Composable
fun SevenDayForecastScreen(
    uiState: WeatherUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val weather = uiState.weatherData
    val dailyList = weather?.daily.orEmpty()
    val tempUnit = uiState.userSettings.temperatureUnit

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
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
                    modifier = Modifier.testTag("forecast_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column {
                    Text(
                        text = "7-Day Detailed Forecast",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = uiState.currentLocation.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            // Daily List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("full_seven_day_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(dailyList) { index, item ->
                    FullDailyForecastCard(
                        item = item,
                        tempUnit = tempUnit,
                        isToday = index == 0
                    )
                }
            }
        }
    }
}

@Composable
private fun FullDailyForecastCard(
    item: DailyForecastItem,
    tempUnit: TemperatureUnit,
    isToday: Boolean
) {
    val condition = WeatherConditionMapper.getCondition(item.weatherCode, isDay = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = if (isToday) 0.16f else 0.10f))
            .border(
                width = if (isToday) 1.5.dp else 1.dp,
                color = if (isToday) Color(0xFF64B5F6).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
            .testTag("forecast_card_${item.dayOfWeek.lowercase()}")
    ) {
        // Top row: Day title, condition, icon, and temperatures
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(condition.primaryAccentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = condition.icon,
                    contentDescription = condition.title,
                    tint = condition.primaryAccentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isToday) "Today (${item.dayOfWeek})" else item.dayOfWeek,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = condition.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Max / Min Temperatures
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = UnitFormatters.formatTemperature(item.maxTempC, tempUnit),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Low: ${UnitFormatters.formatTemperature(item.minTempC, tempUnit)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.65f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Description line
        Text(
            text = condition.description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom detail pills: Rain chance, Sunrise, Sunset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rain pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x332196F3))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = "Rain probability",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.precipitationProbabilityMax}% Rain Chance",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFBBDEFB),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Sunrise pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WbSunny,
                    contentDescription = "Sunrise",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.sunrise,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }

            // Sunset pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WbTwilight,
                    contentDescription = "Sunset",
                    tint = Color(0xFFFF8A80),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.sunset,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}
