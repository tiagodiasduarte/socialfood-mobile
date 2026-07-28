package pt.socialfood.presentation.author.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun AuthorCardPlaceholder(alpha: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AuthorCardHeight)
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(MaterialTheme.colorScheme.surface)
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

            Column(modifier = Modifier.weight(1f)) {
                ShimmerBox(modifier = Modifier.width(140.dp).height(20.dp), alpha = alpha)

                Spacer(Modifier.height(SpaceSize.large))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(3) { index ->
                        StatItemPlaceholder(alpha = alpha)
                        if (index < 2) {
                            ShimmerBox(modifier = Modifier.width(1.dp).height(16.dp), alpha = alpha)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItemPlaceholder(alpha: Float) {
    Column(horizontalAlignment = Alignment.Start) {
        ShimmerBox(modifier = Modifier.width(24.dp).height(18.dp), alpha = alpha)
        ShimmerBox(modifier = Modifier.width(50.dp).height(14.dp), alpha = alpha)
    }
}

@Preview
@Composable
private fun AuthorCardPlaceholderPreview() {
    AppTheme {
        AuthorCardPlaceholder(alpha = rememberShimmerAlpha())
    }
}
