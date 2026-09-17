package pt.socialfood.presentation.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.placeholder.RestaurantCardPlaceholder
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.IconSize
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.user_image_content_description

internal val CardHeight = 125.dp
private const val IMAGE_SIZE = 95

@Composable
fun RestaurantSmallCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    removeButtonContentDescription: String? = null,
    onClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(CardHeight)
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            RestaurantCardInfo(restaurant = restaurant, modifier = Modifier.weight(1f))

            if (removeButtonContentDescription != null) {
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = removeButtonContentDescription,
                        tint = FavouriteRed,
                    )
                }
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
        val imageUrl = restaurant.imagesUrl.firstOrNull()
        Box(
            modifier = Modifier
                .size(IMAGE_SIZE.dp)
                .clip(RoundedCornerShape(SpaceSize.medium)),
        ) {
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
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

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            Text(
                text = restaurant.name,
                style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            RestaurantLocation(restaurant)
            RestaurantRating(restaurant)
        }
    }
}

@Composable
@Preview
private fun RestaurantSmallCardPreview() {
    AppTheme {
        RestaurantSmallCard(
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
            removeButtonContentDescription = "Remove",
        )
    }
}
