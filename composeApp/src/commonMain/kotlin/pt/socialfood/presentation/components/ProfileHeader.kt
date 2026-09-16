package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.buttons.social.SocialButtons
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.ProfileGradientEnd
import pt.socialfood.ui.theme.ProfileGradientStart
import pt.socialfood.ui.theme.SpaceSize

val ProfileHeaderHeight = 150.dp
val ProfileAvatarSize = 72.dp
val ProfileAvatarRingSize = 81.dp
val ProfileAvatarOverlap = ProfileAvatarRingSize / 2

@Composable
fun ProfileHeader(
    name: String,
    username: String,
    imageUrl: String?,
    facebookUrl: String?,
    instagramUrl: String?,
    youtubeUrl: String?,
    modifier: Modifier = Modifier,
    topAction: @Composable BoxScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileHeaderHeight + ProfileAvatarOverlap),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ProfileHeaderHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ProfileGradientStart,
                                ProfileGradientEnd,
                            ),
                        ),
                    ),
                content = topAction,
            )

            SocialButtons(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(vertical = ProfileAvatarOverlap + SpaceSize.medium, horizontal = SpaceSize.large),
                facebookUrl = facebookUrl,
                instagramUrl = instagramUrl,
                youtubeUrl = youtubeUrl,
            )

            UserImage(
                imageUrl = imageUrl,
                modifier = Modifier.padding(horizontal = SpaceSize.large).align(Alignment.BottomStart),
            )
        }

        ProfileDetails(name = name, username = username)
    }
}

@Composable
private fun UserImage(imageUrl: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(ProfileAvatarRingSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            UserImage(
                imageUrl = imageUrl,
                imageSize = ProfileAvatarSize,
            )
        }
    }
}

@Composable
private fun ProfileDetails(name: String, username: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = SpaceSize.large),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "@$username",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        StatsRow(modifier = Modifier.padding(horizontal = SpaceSize.large))
    }
}

@Preview
@Composable
private fun ProfileHeaderPreview() {
    AppTheme {
        ProfileHeader(
            name = "John Doe",
            username = "johndoe",
            imageUrl = null,
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            youtubeUrl = null,
        )
    }
}
