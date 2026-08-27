package pt.socialfood.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.painterResource
import pt.socialfood.ui.theme.ProfileGradientEnd
import pt.socialfood.ui.theme.ProfileGradientStart
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.user_avatar_placeholder

@Composable
fun UserImage(name: String, imageUrl: String?, imageSize: Dp, modifier: Modifier = Modifier) {
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
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(imageSize),
                loading = {
                    Image(
                        painter = painterResource(Res.drawable.user_avatar_placeholder),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
                error = {
                    Image(
                        painter = painterResource(Res.drawable.user_avatar_placeholder),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.user_avatar_placeholder),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
