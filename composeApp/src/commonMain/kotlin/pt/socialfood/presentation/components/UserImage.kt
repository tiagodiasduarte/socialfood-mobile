package pt.socialfood.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import socialfood.composeapp.generated.resources.user_avatar_placeholder
import socialfood.composeapp.generated.resources.user_image_content_description

@Composable
fun UserImage(imageUrl: String?, imageSize: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(ProfileGradientStart, ProfileGradientEnd),
                ),
            ),
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
        painter = painterResource(Res.drawable.user_avatar_placeholder),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
    )
}
