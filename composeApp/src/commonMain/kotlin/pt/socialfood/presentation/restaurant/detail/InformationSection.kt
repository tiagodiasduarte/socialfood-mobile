package pt.socialfood.presentation.restaurant.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.google_icon
import socialfood.composeapp.generated.resources.map_navigation_icon
import socialfood.composeapp.generated.resources.restaurant_detail_information_title
import socialfood.composeapp.generated.resources.restaurant_detail_navigate_description
import socialfood.composeapp.generated.resources.restaurant_detail_reviews_count
import socialfood.composeapp.generated.resources.sign_in_google_button_description
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize


@Composable
internal fun InformationSection(
    restaurant: Restaurant,
    onNavigateClick: () -> Unit,
    onWebsiteClick: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(Res.string.restaurant_detail_information_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = SpaceSize.large),
        )

        Spacer(Modifier.height(SpaceSize.large))

        InformationCard(
            restaurant = restaurant,
            onNavigateClick = onNavigateClick,
            onWebsiteClick = onWebsiteClick
        )

        Spacer(Modifier.height(SpaceSize.xlarge))

        GoogleDataCard(restaurant)
    }
}

@Composable
private fun InformationCard(
    restaurant: Restaurant,
    onNavigateClick: () -> Unit,
    onWebsiteClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = SpaceSize.large),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column {
            if (restaurant.address.isNotBlank()) {
                InfoRow(
                    icon = Icons.Outlined.LocationOn,
                    text = restaurant.address,
                    trailingIcon = painterResource(Res.drawable.map_navigation_icon),
                    trailingIconTint = MaterialTheme.colorScheme.primary,
                    onTrailingClick = onNavigateClick,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            if (restaurant.phoneNumber.isNotBlank()) {
                InfoRow(
                    icon = Icons.Outlined.Phone,
                    text = restaurant.phoneNumber,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            if (restaurant.websiteUrl.isNotBlank()) {
                InfoRow(
                    icon = Icons.Outlined.Language,
                    text = restaurant.websiteUrl,
                    textColor = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    onClick = onWebsiteClick,
                )
            }
        }
    }
}


@Composable
private fun GoogleDataCard(
    restaurant: Restaurant,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = SpaceSize.large),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(modifier = Modifier.padding(SpaceSize.large)) {
            Row {
                Image(
                    painter = painterResource(Res.drawable.google_icon),
                    contentDescription = stringResource(Res.string.sign_in_google_button_description),
                    modifier = Modifier.size(24.dp),
                )

                Spacer(modifier = Modifier.width(SpaceSize.large))

                Text(
                    text = "Verified by Google",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.medium))

            RatingItem(restaurant.rating, restaurant.userRatingCount)
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textDecoration: TextDecoration? = null,
    trailingIcon: Painter? = null,
    trailingIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {},
    onTrailingClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSize.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            textDecoration = textDecoration,
            modifier = Modifier.weight(1f),
        )
        if (trailingIcon != null) {
            Icon(
                painter = trailingIcon,
                contentDescription = stringResource(Res.string.restaurant_detail_navigate_description),
                tint = trailingIconTint,
                modifier = Modifier.size(18.dp).clickable(onClick = onTrailingClick),
            )
        }
    }
}

@Composable
fun RatingItem(rating: Double, reviewCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color = GreyBackground)
                .padding(SpaceSize.small)
        ) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = rating.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

        }

        Spacer(modifier = Modifier.width(SpaceSize.large))

        Text(
            text = stringResource(Res.string.restaurant_detail_reviews_count, reviewCount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun InformationSectionPreview() {
    AppTheme {
        InformationSection(
            restaurant = Restaurant(
                id = "r1",
                name = "Le Jardin Français",
                description = "",
                cuisine = "French Cuisine",
                city = "Lisbon",
                country = "Portugal",
                countryCode = "PT",
                postalCode = "1100-001",
                photoNames = emptyList(),
                address = "123 Gourmet Street, Downtown, Lisbon",
                rating = 4.8,
                userRatingCount = 342,
                websiteUrl = "www.lejardin.com",
                phoneNumber = "+351 910 000 000",
            ),
            onNavigateClick = {},
            onWebsiteClick = {},
        )
    }
}
