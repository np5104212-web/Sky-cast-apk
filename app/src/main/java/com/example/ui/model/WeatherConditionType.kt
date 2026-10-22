package com.example.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class WeatherCategory {
    CLEAR_DAY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    CLOUDY,
    FOG,
    RAIN,
    SNOW,
    THUNDERSTORM
}

data class WeatherConditionInfo(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: WeatherCategory,
    val backgroundBrush: Brush,
    val primaryAccentColor: Color,
    val cardBackground: Color
)

object WeatherConditionMapper {

    fun getCondition(code: Int, isDay: Boolean = true): WeatherConditionInfo {
        val category = when (code) {
            0 -> if (isDay) WeatherCategory.CLEAR_DAY else WeatherCategory.CLEAR_NIGHT
            1, 2 -> if (isDay) WeatherCategory.PARTLY_CLOUDY_DAY else WeatherCategory.PARTLY_CLOUDY_NIGHT
            3 -> WeatherCategory.CLOUDY
            45, 48 -> WeatherCategory.FOG
            51, 53, 55, 56, 57 -> WeatherCategory.RAIN
            61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCategory.RAIN
            71, 73, 75, 77, 85, 86 -> WeatherCategory.SNOW
            95, 96, 99 -> WeatherCategory.THUNDERSTORM
            else -> if (isDay) WeatherCategory.CLEAR_DAY else WeatherCategory.CLEAR_NIGHT
        }

        val (title, description) = when (code) {
            0 -> "Clear Sky" to if (isDay) "Bright sunshine and clear skies" else "Starry clear night sky"
            1 -> "Mainly Clear" to "Scattered light clouds across the sky"
            2 -> "Partly Cloudy" to "Sun and clouds alternating throughout the day"
            3 -> "Overcast" to "Full cloud cover blanket across the area"
            45 -> "Fog" to "Reduced visibility due to atmospheric fog"
            48 -> "Depositing Rime Fog" to "Icy fog with reduced visibility"
            51 -> "Light Drizzle" to "Fine, gentle water droplets falling"
            53 -> "Moderate Drizzle" to "Steady fine drizzle"
            55 -> "Dense Drizzle" to "Heavy persistent drizzle"
            56 -> "Light Freezing Drizzle" to "Cold drizzle freezing on cold surfaces"
            57 -> "Dense Freezing Drizzle" to "Heavy freezing drizzle"
            61 -> "Slight Rain" to "Light intermittent rain showers"
            63 -> "Moderate Rain" to "Steady rainfall across the region"
            65 -> "Heavy Rain" to "Intense downpour with heavy precipitation"
            66 -> "Light Freezing Rain" to "Freezing raindrops creating slick ground"
            67 -> "Heavy Freezing Rain" to "Hazardous heavy freezing rain"
            71 -> "Slight Snow" to "Gentle dusting of snowflakes"
            73 -> "Moderate Snow" to "Steady continuous snowfall"
            75 -> "Heavy Snow" to "Heavy accumulating snow"
            77 -> "Snow Grains" to "Tiny icy snow crystals"
            80 -> "Slight Showers" to "Passing brief rain showers"
            81 -> "Moderate Showers" to "Passing soaking rain showers"
            82 -> "Violent Showers" to "Heavy, sudden convective rain squalls"
            85 -> "Slight Snow Showers" to "Brief intermittent snow flurries"
            86 -> "Heavy Snow Showers" to "Intense snow squalls"
            95 -> "Thunderstorm" to "Thunder, lightning, and gusty winds"
            96 -> "Thunderstorm & Hail" to "Thunderstorm with small hail stones"
            99 -> "Heavy Thunderstorm" to "Severe thunderstorm with large hail"
            else -> "Fair" to "Pleasant weather conditions"
        }

        val icon = when (category) {
            WeatherCategory.CLEAR_DAY -> Icons.Rounded.WbSunny
            WeatherCategory.CLEAR_NIGHT -> Icons.Rounded.NightsStay
            WeatherCategory.PARTLY_CLOUDY_DAY -> Icons.Rounded.WbCloudy
            WeatherCategory.PARTLY_CLOUDY_NIGHT -> Icons.Rounded.CloudQueue
            WeatherCategory.CLOUDY -> Icons.Rounded.Cloud
            WeatherCategory.FOG -> Icons.Rounded.Grain
            WeatherCategory.RAIN -> Icons.Rounded.WaterDrop
            WeatherCategory.SNOW -> Icons.Rounded.AcUnit
            WeatherCategory.THUNDERSTORM -> Icons.Rounded.Thunderstorm
        }

        val (brush, accent, cardBg) = when (category) {
            WeatherCategory.CLEAR_DAY -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0277BD), // Vibrant Azure
                        Color(0xFF0091EA), // Sky Blue
                        Color(0xFF00B0FF), // Light Cyan Blue
                        Color(0xFF80D8FF)  // Soft Horizon
                    )
                ),
                Color(0xFFFFD54F),
                Color(0x26FFFFFF)
            )
            WeatherCategory.CLEAR_NIGHT -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A1128), // Deep Midnight
                        Color(0xFF101F42), // Royal Navy
                        Color(0xFF1C2D5A), // Indigo
                        Color(0xFF283B6B)  // Soft Slate
                    )
                ),
                Color(0xFF90CAF9),
                Color(0x261E293B)
            )
            WeatherCategory.PARTLY_CLOUDY_DAY -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D47A1), // Deep Blue
                        Color(0xFF1976D2), // Classic Sky
                        Color(0xFF42A5F5), // Bright Blue
                        Color(0xFF90CAF9)  // Gentle Cloud Blue
                    )
                ),
                Color(0xFFFFE082),
                Color(0x28FFFFFF)
            )
            WeatherCategory.PARTLY_CLOUDY_NIGHT -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color(0xFF24344D),
                        Color(0xFF334A6A)
                    )
                ),
                Color(0xFF81D4FA),
                Color(0x261E293B)
            )
            WeatherCategory.CLOUDY -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2C3E50), // Slate Cloud
                        Color(0xFF34495E),
                        Color(0xFF4A6572),
                        Color(0xFF6C7A89)
                    )
                ),
                Color(0xFFB0BEC5),
                Color(0x3337474F)
            )
            WeatherCategory.FOG -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF37474F),
                        Color(0xFF546E7A),
                        Color(0xFF78909C),
                        Color(0xFF90A4AE)
                    )
                ),
                Color(0xFFCFD8DC),
                Color(0x33455A64)
            )
            WeatherCategory.RAIN -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F2027), // Moody Stormy Dark
                        Color(0xFF203A43),
                        Color(0xFF2C5364),
                        Color(0xFF3B6B82)
                    )
                ),
                Color(0xFF4FC3F7),
                Color(0x2E102A38)
            )
            WeatherCategory.SNOW -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF283E51),
                        Color(0xFF4B79A1),
                        Color(0xFF7CA6D0),
                        Color(0xFFB5D0EB)
                    )
                ),
                Color(0xFFE1F5FE),
                Color(0x332B3A4A)
            )
            WeatherCategory.THUNDERSTORM -> Triple(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF141E30), // Dark Thunder Sky
                        Color(0xFF243B55),
                        Color(0xFF323B5C),
                        Color(0xFF473E66)  // Purple lightning atmosphere
                    )
                ),
                Color(0xFFFFEE58),
                Color(0x33221C38)
            )
        }

        return WeatherConditionInfo(
            title = title,
            description = description,
            icon = icon,
            category = category,
            backgroundBrush = brush,
            primaryAccentColor = accent,
            cardBackground = cardBg
        )
    }
}
