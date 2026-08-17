package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.buttons.social.SocialButtonsSkeleton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.ProfileGradientEnd
import pt.socialfood.ui.theme.ProfileGradientStart
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun ProfileHeaderSkeleton(
    alpha: Float,
    modifier: Modifier = Modifier,
    topAction: @Composable BoxScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileHeaderHeight + ProfileAvatarOverlap),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ProfileHeaderHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ProfileGradientStart.copy(alpha = 0.5f),
                                ProfileGradientEnd.copy(alpha = 0.5f),
                            ),
                        ),
                    ),
                content = topAction,
            )

            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Box(
                    modifier = Modifier
                        .size(ProfileAvatarRingSize)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    ShimmerBox(
                        modifier = Modifier.size(ProfileAvatarSize),
                        alpha = alpha,
                        shape = CircleShape,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSize.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
            ) {
                ShimmerBox(modifier = Modifier.width(160.dp).height(22.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.width(100.dp).height(16.dp), alpha = alpha)
            }

            SocialButtonsSkeleton(alpha = alpha)

            StatsRowSkeleton(alpha = alpha)
        }
    }
}

@Composable
private fun StatsRowSkeleton(alpha: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
            ) {
                ShimmerBox(modifier = Modifier.width(32.dp).height(18.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.width(52.dp).height(12.dp), alpha = alpha)
            }
            if (index < 2) {
                ShimmerBox(
                    modifier = Modifier
                        .padding(horizontal = SpaceSize.medium)
                        .width(1.dp)
                        .height(32.dp),
                    alpha = alpha,
                )
            }
        }
    }
}

@Composable
fun BoxScope.ProfileHeaderTopActionSkeleton(alpha: Float, modifier: Modifier = Modifier) {
    ShimmerBox(
        modifier = modifier
            .padding(SpaceSize.large)
            .size(40.dp),
        alpha = alpha,
        shape = CircleShape,
    )
}

@Preview
@Composable
private fun ProfileHeaderSkeletonPreview() {
    AppTheme {
        ProfileHeaderSkeleton(alpha = 0.6f) {
            ProfileHeaderTopActionSkeleton(alpha = 0.6f)
        }
    }
}
