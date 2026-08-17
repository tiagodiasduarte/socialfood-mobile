package pt.socialfood.presentation.guide.detail.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun RestaurantItemCardSkeleton(modifier: Modifier = Modifier, alpha: Float) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(Color.White)
            .padding(SpaceSize.large),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(SpaceSize.medium)),
                alpha = alpha,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            ) {
                ShimmerBox(modifier = Modifier.width(130.dp).height(14.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.width(80.dp).height(12.dp), alpha = alpha)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
                ) {
                    ShimmerBox(modifier = Modifier.size(14.dp), alpha = alpha)
                    ShimmerBox(modifier = Modifier.width(32.dp).height(16.dp), alpha = alpha)
                }
            }
        }
    }
}

@Preview
@Composable
private fun RestaurantItemCardSkeletonPreview() {
    AppTheme {
        RestaurantItemCardSkeleton(alpha = rememberShimmerAlpha())
    }
}
