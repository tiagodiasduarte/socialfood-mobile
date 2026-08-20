package pt.socialfood.presentation.favourite.restaurant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.presentation.restaurant.CardHeight
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

private const val SKELETON_ITEM_COUNT = 5
private const val PRIMARY_LINE_WIDTH_FRACTION = 0.7f
private const val SECONDARY_LINE_WIDTH_FRACTION = 0.5f

@Composable
fun FavouriteRestaurantsSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(horizontal = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        repeat(SKELETON_ITEM_COUNT) {
            Card(
                modifier = Modifier.fillMaxWidth().height(CardHeight),
                shape = RoundedCornerShape(SpaceSize.large),
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
                    ShimmerBox(
                        modifier = Modifier.size(95.dp),
                        alpha = alpha,
                        shape = RoundedCornerShape(SpaceSize.medium),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                    ) {
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth(PRIMARY_LINE_WIDTH_FRACTION).height(16.dp),
                            alpha = alpha,
                        )
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth(SECONDARY_LINE_WIDTH_FRACTION).height(14.dp),
                            alpha = alpha,
                        )
                    }
                }
            }
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
