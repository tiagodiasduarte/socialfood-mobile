package pt.socialfood.presentation.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.RestaurantImage
import pt.socialfood.presentation.components.buttons.OutlinedButton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.IconSize
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.restaurant_card_remove_button
import socialfood.composeapp.generated.resources.restaurant_card_visited_button
import socialfood.composeapp.generated.resources.user_image_content_description

val VisitStatusCardHeight = 175.dp
private val IMAGE_SIZE = 85.dp

@Composable
fun RestaurantVisitStatusCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRemoveClick: (() -> Unit)? = null,
    onMoveToVisitedClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .height(VisitStatusCardHeight)
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            ) {
                RestaurantImage(
                    imageUrl = restaurant.imagesUrl.firstOrNull(),
                    contentDescription = stringResource(Res.string.user_image_content_description),
                    modifier = Modifier
                        .size(IMAGE_SIZE)
                        .clip(RoundedCornerShape(SpaceSize.medium)),
                    iconSize = IconSize.small,
                )
                RestaurantCardInfo(
                    modifier = Modifier.weight(1f),
                    restaurant = restaurant,
                )
            }

            Spacer(Modifier.height(SpaceSize.large))

            BottomButtons(
                modifier = Modifier.fillMaxWidth(),
                onRemoveClick = onRemoveClick,
                onMoveToVisitedClick = onMoveToVisitedClick,
            )
        }
    }
}

@Composable
private fun RestaurantCardInfo(restaurant: Restaurant, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        Text(
            text = restaurant.name,
            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
        )

        RestaurantLocation(restaurant)

        RestaurantRating(restaurant)
    }
}

@Composable
private fun BottomButtons(
    modifier: Modifier = Modifier,
    onRemoveClick: (() -> Unit)? = null,
    onMoveToVisitedClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        onMoveToVisitedClick?.let {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.restaurant_card_visited_button),
                icon = Icons.Outlined.CheckCircle,
                onClick = it,
            )
        }

        onRemoveClick?.let {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.restaurant_card_remove_button),
                icon = Icons.Outlined.Delete,
                onClick = it,
            )
        }
    }
}

@Composable
@Preview
private fun RestaurantSmallCardWishlistPreview() {
    AppTheme {
        RestaurantVisitStatusCard(
            restaurant = previewRestaurant,
            onMoveToVisitedClick = {},
        )
    }
}

@Composable
@Preview
private fun RestaurantVisitStatusCardPreview() {
    AppTheme {
        RestaurantVisitStatusCard(
            restaurant = previewRestaurant,
        )
    }
}

private val previewRestaurant = Restaurant(
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
)
