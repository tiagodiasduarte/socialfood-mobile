package pt.socialfood.presentation.favourite.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.presentation.restaurant.RestaurantSmallCardSkeleton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

private const val SKELETON_ITEM_COUNT = 8

@Composable
fun FavouriteRestaurantsSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        repeat(SKELETON_ITEM_COUNT) {
            RestaurantSmallCardSkeleton(alpha = alpha, showRemoveButton = true)
        }
    }
}

@Preview
@Composable
private fun FavouriteRestaurantsSkeletonPreview() {
    AppTheme {
        FavouriteRestaurantsSkeleton()
    }
}
