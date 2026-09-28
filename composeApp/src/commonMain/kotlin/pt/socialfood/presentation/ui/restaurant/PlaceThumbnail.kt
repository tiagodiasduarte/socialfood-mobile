package pt.socialfood.presentation.ui.restaurant

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.RestaurantImage
import pt.socialfood.ui.theme.IconSize

@Composable
fun PlaceThumbnail(imageUrl: String?) {
    RestaurantImage(
        imageUrl = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(10.dp)),
        iconSize = IconSize.small,
    )
}
