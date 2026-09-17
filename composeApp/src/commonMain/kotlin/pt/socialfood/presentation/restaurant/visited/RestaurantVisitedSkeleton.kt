package pt.socialfood.presentation.restaurant.visited

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
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

private const val SKELETON_ITEM_COUNT = 8
private val IMAGE_SIZE = 85.dp
private val ButtonHeight = 40.dp
private const val PRIMARY_LINE_WIDTH_FRACTION = 0.7f
private const val SECONDARY_LINE_WIDTH_FRACTION = 0.5f
private const val TERTIARY_LINE_WIDTH_FRACTION = 0.4f

@Composable
fun RestaurantVisitedSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        repeat(SKELETON_ITEM_COUNT) {
            VisitedCardSkeleton(alpha = alpha)
        }
    }
}

@Composable
private fun VisitedCardSkeleton(alpha: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
        ) {
            VisitedCardInfoRowSkeleton(alpha = alpha)

            Spacer(Modifier.height(SpaceSize.large))

            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(ButtonHeight),
                alpha = alpha,
                shape = RoundedCornerShape(SpaceSize.medium),
            )
        }
    }
}

@Composable
private fun VisitedCardInfoRowSkeleton(alpha: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        ShimmerBox(
            modifier = Modifier.size(IMAGE_SIZE),
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
            ShimmerBox(
                modifier = Modifier.fillMaxWidth(TERTIARY_LINE_WIDTH_FRACTION).height(12.dp),
                alpha = alpha,
            )
        }
        ShimmerBox(
            modifier = Modifier.width(60.dp).height(14.dp),
            alpha = alpha,
        )
    }
}

@Preview
@Composable
private fun RestaurantVisitedSkeletonPreview() {
    AppTheme {
        RestaurantVisitedSkeleton()
    }
}
