package pt.socialfood.presentation.restaurant

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import pt.socialfood.ui.theme.Star

@Composable
fun RestaurantRating(restaurant: Restaurant, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Star,
            modifier = Modifier.size(14.dp),
        )

        Spacer(Modifier.width(SpaceSize.small))

        Text(
            text = restaurant.rating.toString(),
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.width(SpaceSize.small))

        Text(
            text = "(${restaurant.userRatingCount})",
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
