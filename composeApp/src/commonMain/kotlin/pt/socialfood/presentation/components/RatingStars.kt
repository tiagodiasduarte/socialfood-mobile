package pt.socialfood.presentation.restaurants.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Double,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 20.dp
) {
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
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(starSize)
            )
        }
    }
}
