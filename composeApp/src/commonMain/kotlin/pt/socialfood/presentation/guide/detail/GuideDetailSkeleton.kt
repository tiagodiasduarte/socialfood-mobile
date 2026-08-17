package pt.socialfood.presentation.guide.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.ShimmerColor
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.presentation.guide.detail.author.AuthorItemCardSkeleton
import pt.socialfood.presentation.guide.detail.restaurant.RestaurantItemCardSkeleton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun GuideDetailSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GreyBackground),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        item {
            HeroImageSkeleton(alpha = alpha)

            Spacer(Modifier.height(SpaceSize.large))

            Row(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBox(
                    modifier = Modifier.width(70.dp).height(24.dp),
                    alpha = alpha,
                    shape = RoundedCornerShape(SpaceSize.medium),
                )
                ShimmerBox(
                    modifier = Modifier.width(1.dp).height(16.dp),
                    alpha = alpha,
                )
                ShimmerBox(
                    modifier = Modifier.width(100.dp).height(14.dp),
                    alpha = alpha,
                )
            }

            Spacer(Modifier.height(SpaceSize.large))

            ShimmerBox(
                modifier = Modifier
                    .padding(horizontal = SpaceSize.large)
                    .fillMaxWidth()
                    .height(28.dp),
                alpha = alpha,
            )

            Spacer(Modifier.height(SpaceSize.large))

            Column(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
            ) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().height(14.dp),
                    alpha = alpha,
                )
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().height(14.dp),
                    alpha = alpha,
                )
                ShimmerBox(
                    modifier = Modifier.width(200.dp).height(14.dp),
                    alpha = alpha,
                )
            }

            Spacer(Modifier.height(SpaceSize.large))

            AuthorItemCardSkeleton(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                alpha = alpha,
            )

            Spacer(Modifier.height(SpaceSize.xlarge))

            ShimmerBox(
                modifier = Modifier
                    .padding(horizontal = SpaceSize.large)
                    .width(140.dp)
                    .height(18.dp),
                alpha = alpha,
            )

            Spacer(Modifier.height(SpaceSize.large))
        }

        items(4) {
            RestaurantItemCardSkeleton(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                alpha = alpha,
            )
        }

        item { Spacer(Modifier.height(SpaceSize.xxlarge)) }
    }
}

@Composable
private fun HeroImageSkeleton(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(GuideImageHeight)
            .alpha(alpha)
            .background(ShimmerColor),
    ) {
        ShimmerBox(
            modifier = Modifier
                .padding(SpaceSize.large)
                .size(40.dp),
            alpha = 1f,
            shape = CircleShape,
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            ShimmerBox(modifier = Modifier.size(40.dp), alpha = 1f, shape = CircleShape)
            ShimmerBox(modifier = Modifier.size(40.dp), alpha = 1f, shape = CircleShape)
        }
    }
}

@Preview
@Composable
private fun GuideDetailSkeletonPreview() {
    AppTheme {
        GuideDetailSkeleton()
    }
}
