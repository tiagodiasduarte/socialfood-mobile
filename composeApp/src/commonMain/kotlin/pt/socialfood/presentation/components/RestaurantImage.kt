package pt.socialfood.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.SubcomposeAsyncImage
import pt.socialfood.presentation.components.placeholder.RestaurantCardPlaceholder
import pt.socialfood.ui.theme.IconSize

@Composable
fun RestaurantImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = IconSize.medium,
) {
    Box(modifier = modifier) {
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { RestaurantCardPlaceholder(iconSize) },
                error = { RestaurantCardPlaceholder(iconSize) },
            )
        } else {
            RestaurantCardPlaceholder(iconSize)
        }
    }
}
