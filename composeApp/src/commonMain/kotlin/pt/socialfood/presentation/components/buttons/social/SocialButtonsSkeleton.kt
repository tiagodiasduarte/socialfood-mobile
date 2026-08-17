package pt.socialfood.presentation.components.buttons.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme

private const val SOCIAL_BUTTON_COUNT = 3

@Composable
fun SocialButtonsSkeleton(alpha: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SocialButtonsRowSpacing),
    ) {
        repeat(SOCIAL_BUTTON_COUNT) {
            ShimmerBox(
                modifier = Modifier.size(SocialButtonsIconSize),
                alpha = alpha,
                shape = CircleShape,
            )
        }
    }
}

@Preview
@Composable
private fun SocialButtonsSkeletonPreview() {
    AppTheme {
        SocialButtonsSkeleton(alpha = rememberShimmerAlpha())
    }
}
