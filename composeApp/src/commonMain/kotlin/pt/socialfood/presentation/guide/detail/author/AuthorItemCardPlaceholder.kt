package pt.socialfood.presentation.guide.detail.author

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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

private val CardHeight = 80.dp

@Composable
fun AuthorItemCardPlaceholder(alpha: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = SpaceSize.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            ShimmerBox(modifier = Modifier.size(44.dp), alpha = alpha, shape = CircleShape)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                ShimmerBox(modifier = Modifier.width(140.dp).height(16.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.width(90.dp).height(12.dp), alpha = alpha)
            }
        }
    }
}

@Composable
@Preview
private fun AuthorItemCardPlaceholderPreview() {
    AppTheme {
        AuthorItemCardPlaceholder(alpha = rememberShimmerAlpha())
    }
}
