package pt.socialfood.presentation.authors.detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ProfileHeaderPlaceholder
import pt.socialfood.presentation.components.ProfileHeaderTopActionPlaceholder
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun AuthorDetailPlaceholder(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GreyBackground),
    ) {
        item {
            ProfileHeaderPlaceholder(alpha = alpha) {
                ProfileHeaderTopActionPlaceholder(alpha = alpha)
            }

            Spacer(Modifier.height(SpaceSize.large))

            ShimmerBox(
                modifier = Modifier
                    .padding(horizontal = SpaceSize.large)
                    .width(100.dp)
                    .height(18.dp),
                alpha = alpha,
            )

            Spacer(Modifier.height(SpaceSize.large))
        }

        items(4) {
            AuthorGuideCardPlaceholder(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                alpha = alpha,
            )
            Spacer(Modifier.height(SpaceSize.large))
        }
    }
}

@Composable
private fun AuthorGuideCardPlaceholder(modifier: Modifier = Modifier, alpha: Float) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(Color.White)
            .padding(SpaceSize.medium),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(SpaceSize.medium)),
                alpha = alpha,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
            ) {
                ShimmerBox(modifier = Modifier.width(140.dp).height(14.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(12.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.width(180.dp).height(12.dp), alpha = alpha)
                Spacer(Modifier.height(SpaceSize.small))
                ShimmerBox(modifier = Modifier.width(80.dp).height(11.dp), alpha = alpha)
            }
        }
    }
}

@Preview
@Composable
private fun AuthorDetailPlaceholderPreview() {
    AppTheme {
        AuthorDetailPlaceholder()
    }
}
