package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.cardImageScrim(): Modifier = background(
    Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.75f),
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY,
    ),
)
