package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun ProfileHeaderPlaceholder(
    alpha: Float,
    modifier: Modifier = Modifier,
    topAction: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
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
                            Color(0xFFF05A1A).copy(alpha = 0.5f),
                            Color(0xFFB82010).copy(alpha = 0.5f),
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
}

@Composable
fun BoxScope.ProfileHeaderTopActionPlaceholder(alpha: Float, modifier: Modifier = Modifier) {
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
private fun ProfileHeaderPlaceholderPreview() {
    AppTheme {
        ProfileHeaderPlaceholder(alpha = 0.6f) {
            ProfileHeaderTopActionPlaceholder(alpha = 0.6f)
        }
    }
}
