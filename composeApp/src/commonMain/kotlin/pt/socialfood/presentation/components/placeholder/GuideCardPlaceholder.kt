package pt.socialfood.presentation.components.placeholder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.resources.painterResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.IconSize
import pt.socialfood.ui.theme.PlaceholderGradientEnd
import pt.socialfood.ui.theme.PlaceholderGradientStart
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_placeholder_icon

@Composable
fun GuideCardPlaceholder(iconSize: Dp = IconSize.medium) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(PlaceholderGradientStart, PlaceholderGradientEnd),
                ),
            ),
    ) {
        Image(
            painter = painterResource(Res.drawable.guide_placeholder_icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize).align(Alignment.Center),
        )
    }
}

@Composable
@Preview
fun GuideCardPlaceholderPreview() {
    AppTheme {
        RestaurantCardPlaceholder()
    }
}
