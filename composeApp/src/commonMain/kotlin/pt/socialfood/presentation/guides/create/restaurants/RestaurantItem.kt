package pt.socialfood.presentation.guides.create.restaurants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.guides.edit.search_restaurants.PlaceThumbnail
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun RestaurantItem(
    restaurant: Restaurant,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        PlaceThumbnail(imageUrl = restaurant.photoNames.firstOrNull())

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun RestaurantItemPreview() {
    AppTheme {
        RestaurantItem(
            restaurant = Restaurant(
                id = "1",
                name = "Belcanto",
                description = "Fine dining restaurant",
                city = "Lisbon",
                country = "Portugal",
                countryCode = "Portugal",
                postalCode = "1234",
                photoNames = emptyList(),
                address = "Largo de São Carlos 10, Lisboa",
                rating = 4.8,
                userRatingCount = 1200,
                websiteUrl = "",
                phoneNumber = "",
            ),
            onRemove = {},
        )
    }
}