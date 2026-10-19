package pt.socialfood.feature.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.socialfood.core.designsystem.component.ShimmerBox
import pt.socialfood.core.designsystem.component.rememberShimmerAlpha
import pt.socialfood.core.designsystem.theme.AppTheme
import pt.socialfood.core.designsystem.theme.SpaceSize
import pt.socialfood.core.ui.guide.GuideCardHeight

private const val SKELETON_ITEM_COUNT = 8

@Composable
fun GuidesSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(horizontal = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        repeat(SKELETON_ITEM_COUNT) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GuideCardHeight),
                alpha = alpha,
                shape = RoundedCornerShape(SpaceSize.large),
            )
        }
    }
}

@Preview
@Composable
private fun GuidesSkeletonPreview() {
    AppTheme {
        GuidesSkeleton()
    }
}
