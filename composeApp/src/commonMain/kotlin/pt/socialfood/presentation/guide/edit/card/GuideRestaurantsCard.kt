package pt.socialfood.presentation.guide.edit.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.guide.create.restaurants.RestaurantItem
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_restaurants_add_button
import socialfood.composeapp.generated.resources.edit_guide_restaurants_empty_hint
import socialfood.composeapp.generated.resources.edit_guide_restaurants_empty_label
import socialfood.composeapp.generated.resources.edit_guide_restaurants_title_label

@Composable
fun GuideRestaurantsCard(restaurants: List<Restaurant>, onAddClick: () -> Unit, onRemoveClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSize.large),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.edit_guide_restaurants_title_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(SpaceSize.small),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = SpaceSize.large,
                        vertical = SpaceSize.medium,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(SpaceSize.large),
                    )
                    Spacer(Modifier.width(SpaceSize.small))
                    Text(
                        text = stringResource(Res.string.edit_guide_restaurants_add_button),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            if (restaurants.isNotEmpty()) {
                Spacer(Modifier.height(SpaceSize.medium))

                restaurants.forEachIndexed { index, restaurant ->
                    if (index > 0) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    RestaurantItem(
                        restaurant = restaurant,
                        onRemove = { onRemoveClick(restaurant.id) },
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SpaceSize.large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
                ) {
                    Text(
                        text = stringResource(Res.string.edit_guide_restaurants_empty_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.edit_guide_restaurants_empty_hint),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GuideRestaurantsCardEmptyPreview() {
    AppTheme {
        GuideRestaurantsCard(
            restaurants = emptyList(),
            onAddClick = {},
            onRemoveClick = {},
        )
    }
}

@Preview
@Composable
private fun GuideRestaurantsCardWithItemsPreview() {
    AppTheme {
        GuideRestaurantsCard(
            restaurants = listOf(
                Restaurant(
                    id = "1",
                    name = "Belcanto",
                    description = "",
                    city = "Midtown",
                    country = "French",
                    countryCode = "French",
                    postalCode = "French",
                    photoNames = emptyList(),
                    address = "Largo de São Carlos 10, Lisboa",
                    rating = 4.8,
                    userRatingCount = 1200,
                    websiteUrl = "",
                    phoneNumber = "",
                    location = Location(latitude = 38.7106, longitude = -9.1425),
                ),
                Restaurant(
                    id = "2",
                    name = "Time Out Market",
                    description = "",
                    city = "Midtown",
                    country = "French",
                    countryCode = "French",
                    postalCode = "French",
                    photoNames = emptyList(),
                    address = "Av. 24 de Julho 49, Lisboa",
                    rating = 4.5,
                    userRatingCount = 8000,
                    websiteUrl = "",
                    phoneNumber = "",
                    location = Location(latitude = 38.7089, longitude = -9.1469),
                ),
            ),
            onAddClick = {},
            onRemoveClick = {},
        )
    }
}
