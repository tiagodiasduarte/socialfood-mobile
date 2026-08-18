package pt.socialfood.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.StarColor

@Composable
fun RatingStars(rating: Double, modifier: Modifier = Modifier, maxStars: Int = 5, starSize: Dp = 20.dp) {
    Row(modifier = modifier) {
        val fullStars = rating.toInt()
        val hasHalfStar = (rating - fullStars) >= 0.5

        repeat(maxStars) { index ->

            val icon = when {
                index < fullStars -> Icons.Filled.Star
                index == fullStars && hasHalfStar -> Icons.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }

            Icon(
                imageVector = icon,
                contentDescription = "Rating star",
                tint = StarColor,
                modifier = Modifier.size(starSize),
            )
        }
    }
}
