package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.model.WeatherCategory

@Composable
fun WeatherBackground(
    category: WeatherCategory,
    isDarkTheme: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val (topColorTarget, midColorTarget, bottomColorTarget) = when (category) {
        WeatherCategory.CLEAR_DAY -> Triple(
            Color(0xFF0D47A1), // Deep Sky Blue
            Color(0xFF1976D2), // Bright Sky
            Color(0xFF42A5F5)  // Cyan Horizon
        )
        WeatherCategory.CLEAR_NIGHT -> Triple(
            Color(0xFF090D1C), // Deep Space Midnight
            Color(0xFF121B33), // Starry Indigo
            Color(0xFF1E2D52)  // Soft Midnight Blue
        )
        WeatherCategory.PARTLY_CLOUDY_DAY -> Triple(
            Color(0xFF1565C0),
            Color(0xFF1E88E5),
            Color(0xFF64B5F6)
        )
        WeatherCategory.PARTLY_CLOUDY_NIGHT -> Triple(
            Color(0xFF0E1626),
            Color(0xFF1B283E),
            Color(0xFF283A58)
        )
        WeatherCategory.CLOUDY -> Triple(
            Color(0xFF2C3E50),
            Color(0xFF3E5062),
            Color(0xFF5D6D7E)
        )
        WeatherCategory.FOG -> Triple(
            Color(0xFF37474F),
            Color(0xFF546E7A),
            Color(0xFF78909C)
        )
        WeatherCategory.RAIN -> Triple(
            Color(0xFF1A2634),
            Color(0xFF243447),
            Color(0xFF38526E)
        )
        WeatherCategory.SNOW -> Triple(
            Color(0xFF263238),
            Color(0xFF37474F),
            Color(0xFF546E7A)
        )
        WeatherCategory.THUNDERSTORM -> Triple(
            Color(0xFF121526),
            Color(0xFF1D223B),
            Color(0xFF342E54) // Stormy Electric Purple tone
        )
    }

    val topColor by animateColorAsState(
        targetValue = topColorTarget,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bgTop"
    )
    val midColor by animateColorAsState(
        targetValue = midColorTarget,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bgMid"
    )
    val bottomColor by animateColorAsState(
        targetValue = bottomColorTarget,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bgBottom"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, midColor, bottomColor)
                )
            )
    ) {
        content()
    }
}
