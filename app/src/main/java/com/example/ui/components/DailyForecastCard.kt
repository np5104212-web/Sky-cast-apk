package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
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
import com.example.ui.model.WeatherConditionMapper

@Composable
fun DailyForecastPreviewCard(
    dailyItems: List<DailyForecastItem>,
    tempUnit: TemperatureUnit,
    cardBackground: Color,
    onViewFullForecast: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackground.copy(alpha = 0.28f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
            .testTag("daily_forecast_preview_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewFullForecast() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "7-Day Outlook",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("view_full_forecast_button")
            ) {
                Text(
                    text = "Full Details",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "View full 7-day forecast",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        dailyItems.take(5).forEachIndexed { index, item ->
            DailyForecastRowItem(
                item = item,
                tempUnit = tempUnit,
                isToday = index == 0
            )
            if (index < 4) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DailyForecastRowItem(
    item: DailyForecastItem,
    tempUnit: TemperatureUnit,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    val condition = WeatherConditionMapper.getCondition(item.weatherCode, isDay = true)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day column
        Column(modifier = Modifier.width(90.dp)) {
            Text(
                text = if (isToday) "Today" else item.dayOfWeek,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                    color = Color.White
                )
            )
            Text(
                text = item.formattedDate,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            )
        }

        // Icon & Rain probability
        Row(
            modifier = Modifier.width(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = condition.icon,
                contentDescription = condition.title,
                tint = condition.primaryAccentColor,
                modifier = Modifier.size(24.dp)
            )
            if (item.precipitationProbabilityMax > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "${item.precipitationProbabilityMax}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF90CAF9),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Temperature range
        Text(
            text = UnitFormatters.formatTemperature(item.minTempC, tempUnit),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.65f),
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Visual mini bar
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF64B5F6), Color(0xFFFFB74D))
                    )
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = UnitFormatters.formatTemperature(item.maxTempC, tempUnit),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
