package pt.socialfood.presentation.restaurant

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.SpaceSize
import pt.socialfood.ui.theme.StarColor

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    isFavourite: Boolean = false,
    onClick: () -> Unit = {},
    onFavouriteClick: () -> Unit = {},
) {
    Card(
        modifier = (if (width != null) modifier.width(width) else modifier.fillMaxWidth())
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column {
            RestaurantCardImage(
                restaurant = restaurant,
                isFavourite = isFavourite,
                onFavouriteClick = onFavouriteClick,
            )

            RestaurantCardInfo(restaurant = restaurant)
        }
    }
}

@Composable
private fun RestaurantCardImage(restaurant: Restaurant, isFavourite: Boolean, onFavouriteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
    ) {
        val imageUrl = restaurant.photoNames.firstOrNull()
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = "$imageUrl&size=400",
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                },
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
                .clickable(onClick = onFavouriteClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favourite",
                tint = if (isFavourite) FavouriteRed else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun RestaurantCardInfo(restaurant: Restaurant) {
    Column(
        modifier = Modifier.padding(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        Text(
            text = restaurant.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )

            Text(
                text = restaurant.city,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier.size(16.dp),
            )

            Spacer(Modifier.width(SpaceSize.small))

            Text(
                text = restaurant.rating.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.width(SpaceSize.small))

            Text(
                text = "(${restaurant.userRatingCount})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
@Preview
fun RestaurantCardPreview() {
    AppTheme {
        RestaurantCard(
            restaurant = Restaurant(
                id = "r1",
                name = "Le Jardin",
                description = "A charming garden restaurant with French-inspired cuisine",
                city = "Midtown",
                country = "French",
                countryCode = "French",
                postalCode = "French",
                photoNames = emptyList(),
                address = "Rua Augusta 123, Lisbon",
                rating = 4.8,
                userRatingCount = 320,
                websiteUrl = "",
                phoneNumber = "+351 910 000 000",
            ),
        )
    }
}
