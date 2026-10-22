package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyForecastItem
import com.example.data.model.TemperatureUnit
import com.example.ui.model.WeatherConditionMapper

@Composable
fun HourlyForecastRow(
    hourlyItems: List<HourlyForecastItem>,
    tempUnit: TemperatureUnit,
    cardBackground: Color,
    modifier: Modifier = Modifier
) {
    if (hourlyItems.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hourly_forecast_section")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "24-Hour Forecast",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "${hourlyItems.size} hrs",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(hourlyItems) { index, item ->
                val condition = WeatherConditionMapper.getCondition(item.weatherCode, item.isDay)
                val isNow = index == 0

                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isNow) cardBackground.copy(alpha = 0.45f) else cardBackground.copy(alpha = 0.22f))
                        .border(
                            width = if (isNow) 1.5.dp else 1.dp,
                            color = if (isNow) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(vertical = 12.dp, horizontal = 6.dp)
                        .testTag("hourly_item_$index"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.timeLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isNow) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNow) Color.White else Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Icon(
                        imageVector = condition.icon,
                        contentDescription = condition.title,
                        tint = condition.primaryAccentColor,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = UnitFormatters.formatTemperature(item.temperatureC, tempUnit),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )

                    if (item.precipitationProbability > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WaterDrop,
                                contentDescription = "Rain probability",
                                tint = Color(0xFF64B5F6),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${item.precipitationProbability}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF90CAF9),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}
