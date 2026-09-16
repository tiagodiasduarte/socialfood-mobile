package pt.socialfood.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.ProfileGradientEnd
import pt.socialfood.ui.theme.ProfileGradientStart
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.user_image_content_description
import socialfood.composeapp.generated.resources.user_placeholder

@Composable
fun UserImage(imageUrl: String?, imageSize: Dp, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Box(
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(ProfileGradientStart, ProfileGradientEnd),
                ),
            )
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = stringResource(Res.string.user_image_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(imageSize),
                loading = {
                    Placeholder()
                },
                error = {
                    Placeholder()
                },
            )
        } else {
            Placeholder()
        }
    }
}

@Composable
private fun Placeholder() {
    Image(
        painter = painterResource(Res.drawable.user_placeholder),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
    )
}
