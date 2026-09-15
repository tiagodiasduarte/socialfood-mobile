package pt.socialfood.presentation.restaurant.detail

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.map.MapRestaurantView
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.restaurant_detail_location_copy_address_description
import socialfood.composeapp.generated.resources.restaurant_detail_location_expand_description
import socialfood.composeapp.generated.resources.restaurant_detail_location_title

@Composable
internal fun LocationSection(restaurant: Restaurant, onExpandClick: () -> Unit) {
    if (restaurant.address.isBlank()) return

    val clipboardManager = LocalClipboardManager.current

    Column {
        Spacer(Modifier.height(SpaceSize.xlarge))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = SpaceSize.large),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Spacer(Modifier.height(SpaceSize.xlarge))

        Text(
            text = stringResource(Res.string.restaurant_detail_location_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = SpaceSize.large),
        )

        Spacer(Modifier.height(SpaceSize.large))

        LocationMapPreview(restaurant = restaurant, onExpandClick = onExpandClick)

        Spacer(Modifier.height(SpaceSize.large))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = restaurant.address,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = stringResource(Res.string.restaurant_detail_location_copy_address_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { clipboardManager.setText(AnnotatedString(restaurant.address)) },
            )
        }
    }
}

@Composable
private fun LocationMapPreview(restaurant: Restaurant, onExpandClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceSize.large)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        MapRestaurantView(
            restaurants = listOf(restaurant),
            selectedRestaurantId = restaurant.id,
            onRestaurantSelected = {},
            showMarkerLabel = false,
            dragGesturesEnabled = false,
            zoomGesturesEnabled = false,
            onMapClick = onExpandClick,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.medium)
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onExpandClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.OpenInFull,
                contentDescription = stringResource(Res.string.restaurant_detail_location_expand_description),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
