package pt.socialfood.presentation.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
fun AuthorsPlaceholder(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(horizontal = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        repeat(6) {
            AuthorCardPlaceholder(alpha = alpha)
        }
    }
}

@Composable
private fun AuthorCardPlaceholder(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(Color.White)
            .padding(SpaceSize.large),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            ShimmerBox(
                modifier = Modifier.size(56.dp),
                alpha = alpha,
                shape = CircleShape,
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    ShimmerBox(
                        modifier = Modifier.width(130.dp).height(16.dp),
                        alpha = alpha,
                    )
                    ShimmerBox(
                        modifier = Modifier.width(72.dp).height(40.dp),
                        alpha = alpha,
                        shape = RoundedCornerShape(50),
                    )
                }

                Spacer(Modifier.height(SpaceSize.medium))

                ShimmerBox(
                    modifier = Modifier.width(80.dp).height(12.dp),
                    alpha = alpha,
                )

                Spacer(Modifier.height(SpaceSize.large))

                Row(horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                    repeat(3) {
                        ShimmerBox(
                            modifier = Modifier.width(44.dp).height(28.dp),
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
private fun AuthorsPlaceholderPreview() {
    AppTheme {
        AuthorsPlaceholder()
    }
}
