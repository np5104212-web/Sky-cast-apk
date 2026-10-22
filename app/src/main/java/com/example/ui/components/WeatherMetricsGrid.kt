package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Umbrella
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.data.model.TemperatureUnit
import com.example.data.model.WindSpeedUnit

@Composable
fun WeatherMetricsGrid(
    current: CurrentWeather,
    todayForecast: DailyForecastItem?,
    tempUnit: TemperatureUnit,
    windUnit: WindSpeedUnit,
    cardBackground: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("weather_metrics_grid"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Feels like & Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Rounded.Thermostat,
                iconTint = Color(0xFFFFB74D),
                label = "FEELS LIKE",
                value = UnitFormatters.formatTemperatureWithUnit(current.apparentTemperatureC, tempUnit),
                subText = if (current.apparentTemperatureC > current.temperatureC) "Warmer than actual" else "Similar to actual",
                cardBackground = cardBackground,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                icon = Icons.Rounded.Water,
                iconTint = Color(0xFF64B5F6),
                label = "HUMIDITY",
                value = "${current.humidity}%",
                subText = when {
                    current.humidity < 40 -> "Dry air"
                    current.humidity in 40..70 -> "Comfortable"
                    else -> "High humidity"
                },
                cardBackground = cardBackground,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Wind & Precipitation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Rounded.Air,
                iconTint = Color(0xFF81D4FA),
                label = "WIND SPEED",
                value = UnitFormatters.formatWindSpeed(current.windSpeedKmh, windUnit),
                subText = when {
                    current.windSpeedKmh < 10 -> "Light breeze"
                    current.windSpeedKmh < 25 -> "Moderate breeze"
                    else -> "Strong breeze"
                },
                cardBackground = cardBackground,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                icon = Icons.Rounded.WaterDrop,
                iconTint = Color(0xFF4FC3F7),
                label = "PRECIPITATION",
                value = "${current.precipitationMm} mm",
                subText = "Chance: ${todayForecast?.precipitationProbabilityMax ?: 0}%",
                cardBackground = cardBackground,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Rain chance & Sunrise / Sunset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Rounded.Umbrella,
                iconTint = Color(0xFFBA68C8),
                label = "RAIN CHANCE",
                value = "${todayForecast?.precipitationProbabilityMax ?: 0}%",
                subText = if ((todayForecast?.precipitationProbabilityMax ?: 0) > 40) "Carry an umbrella" else "Low chance of rain",
                cardBackground = cardBackground,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                icon = Icons.Rounded.WbSunny,
                iconTint = Color(0xFFFFD54F),
                label = "SUNRISE / SUNSET",
                value = todayForecast?.sunrise ?: "--:--",
                subText = "Sunset: ${todayForecast?.sunset ?: "--:--"}",
                cardBackground = cardBackground,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    subText: String,
    cardBackground: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground.copy(alpha = 0.28f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = subText,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp
            ),
            maxLines = 1
        )
    }
}
