package pt.socialfood.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

private const val HOME_SECTION_COUNT = 3
private const val SECTION_CARD_COUNT = 3

@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.background(GreyBackground),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.xlarge),
    ) {
        repeat(HOME_SECTION_COUNT) {
            HomeSectionSkeleton(alpha = alpha)
        }
    }
}

@Composable
private fun HomeSectionSkeleton(alpha: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.large)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(140.dp)
                    .height(18.dp),
                alpha = alpha,
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
            contentPadding = PaddingValues(horizontal = SpaceSize.large),
        ) {
            items(SECTION_CARD_COUNT) {
                ShimmerBox(
                    modifier = Modifier
                        .width(280.dp)
                        .height(180.dp),
                    alpha = alpha,
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeSkeletonPreview() {
    AppTheme {
        HomeSkeleton()
    }
}
