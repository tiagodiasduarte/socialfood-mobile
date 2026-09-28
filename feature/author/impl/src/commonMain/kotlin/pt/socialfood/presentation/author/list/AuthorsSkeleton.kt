package pt.socialfood.presentation.author.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

private const val AUTHOR_CARD_COUNT = 6

@Composable
fun AuthorsSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier.padding(horizontal = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        repeat(AUTHOR_CARD_COUNT) {
            AuthorCardSkeleton(alpha = alpha)
        }
    }
}

@Preview
@Composable
private fun AuthorsSkeletonPreview() {
    AppTheme {
        AuthorsSkeleton()
    }
}
