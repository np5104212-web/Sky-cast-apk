package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.model.WeatherCategory
import com.example.ui.model.WeatherConditionInfo

@Composable
fun AnimatedWeatherArt(
    conditionInfo: WeatherConditionInfo,
    modifier: Modifier = Modifier,
    iconSize: Dp = 96.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weatherAnim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )

    Box(
        modifier = modifier.size(iconSize * 1.5f),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Aura Canvas
        val auraColor = conditionInfo.primaryAccentColor
        Canvas(
            modifier = Modifier
                .size(iconSize * 1.35f)
                .scale(pulseScale)
                .alpha(auraAlpha)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor,
                        auraColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.width / 2f
                )
            )
        }

        // Icon with gentle floating animation and conditional rotation
        val isSun = conditionInfo.category == WeatherCategory.CLEAR_DAY
        val iconModifier = Modifier
            .size(iconSize)
            .offset(y = floatOffset.dp)
            .then(if (isSun) Modifier.rotate(sunRotation) else Modifier)

        Icon(
            imageVector = conditionInfo.icon,
            contentDescription = conditionInfo.title,
            tint = conditionInfo.primaryAccentColor,
            modifier = iconModifier
        )
    }
}
