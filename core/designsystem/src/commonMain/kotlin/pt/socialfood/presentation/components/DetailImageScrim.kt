package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.detailImageScrim(): Modifier = background(
    Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.2f),
            Color.Transparent,
            Color.Black.copy(alpha = 0.15f),
        ),
    ),
)
