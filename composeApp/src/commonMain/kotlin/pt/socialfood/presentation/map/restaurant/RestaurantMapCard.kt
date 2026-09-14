package pt.socialfood.presentation.map.restaurant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.placeholder.RestaurantCardPlaceholder
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.IconSize
import pt.socialfood.ui.theme.SpaceSize
import pt.socialfood.ui.theme.Star
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.user_image_content_description

@Composable
fun RestaurantMapCard(restaurant: Restaurant, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .width(250.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(SpaceSize.medium)),
            ) {
                if (restaurant.imagesUrl.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = restaurant.imagesUrl[0],
                        contentDescription = stringResource(Res.string.user_image_content_description),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = { RestaurantCardPlaceholder() },
                        error = { RestaurantCardPlaceholder() },
                    )
                } else {
                    RestaurantCardPlaceholder(iconSize = IconSize.small)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
            ) {
                RestaurantCardInfo(restaurant = restaurant, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RestaurantCardInfo(restaurant: Restaurant, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            Text(
                text = restaurant.name,
                maxLines = 1,
                style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            RestaurantLocationRow(restaurant)
            RestaurantRatingRow(restaurant)
        }
    }
}

@Composable
private fun RestaurantLocationRow(restaurant: Restaurant) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "${restaurant.city}, ${restaurant.country}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RestaurantRatingRow(restaurant: Restaurant) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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

@Composable
@Preview
private fun RestaurantMapCardPreview() {
    AppTheme {
        RestaurantMapCard(
            restaurant = Restaurant(
                id = "r1",
                name = "Le Jardin",
                description = "A charming garden restaurant with French-inspired cuisine",
                city = "Lisbon",
                country = "Portugal",
                countryCode = "PT",
                postalCode = "1000-000",
                imagesUrl = emptyList(),
                address = "Rua Augusta 123, Lisbon",
                rating = 4.8,
                userRatingCount = 320,
                websiteUrl = "",
                phoneNumber = "+351 910 000 000",
                location = Location(latitude = 38.7223, longitude = -9.1393),
            ),
        )
    }
}
