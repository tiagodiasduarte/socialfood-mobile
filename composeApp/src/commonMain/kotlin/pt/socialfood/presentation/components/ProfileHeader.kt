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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.buttons.social.SocialButtons
import pt.socialfood.presentation.profile.StatsRow
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

val ProfileHeaderHeight = 180.dp
val ProfileAvatarSize = 96.dp
val ProfileAvatarRingSize = 108.dp
val ProfileAvatarOverlap = ProfileAvatarRingSize / 2

@Composable
fun ProfileHeader(
    name: String,
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
            .background(GreyBackground),
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
                                Color(0xFFF05A1A),
                                Color(0xFFB82010),
                            ),
                        ),
                    ),
                content = topAction,
            )
            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Box(
                    modifier = Modifier
                        .size(ProfileAvatarRingSize)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    UserImage(
                        name = name,
                        imageUrl = imageUrl,
                        imageSize = ProfileAvatarSize,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSize.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )

            StatsRow()

            SocialButtons(
                facebookUrl = facebookUrl,
                instagramUrl = instagramUrl,
                youtubeUrl = youtubeUrl,
            )
        }
    }
}
