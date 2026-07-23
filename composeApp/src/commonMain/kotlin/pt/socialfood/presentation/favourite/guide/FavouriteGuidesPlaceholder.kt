package pt.socialfood.presentation.favourite.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.presentation.guides.CardHeight
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun FavouriteGuidesPlaceholder(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(horizontal = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(5) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CardHeight),
                alpha = alpha,
                shape = RoundedCornerShape(SpaceSize.large),
            )
        }
    }
}

@Preview
@Composable
private fun FavouriteGuidesPlaceholderPreview() {
    AppTheme {
        FavouriteGuidesPlaceholder()
    }
}
