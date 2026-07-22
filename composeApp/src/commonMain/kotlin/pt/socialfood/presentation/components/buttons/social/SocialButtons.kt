package pt.socialfood.presentation.components.buttons.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.facebook_icon
import socialfood.composeapp.generated.resources.instagram_icon
import socialfood.composeapp.generated.resources.social_facebook_icon_description
import socialfood.composeapp.generated.resources.social_instagram_icon_description
import socialfood.composeapp.generated.resources.social_youtube_icon_description
import socialfood.composeapp.generated.resources.youtube_icon
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

internal val SocialLinkIconSize = 36.dp
internal val SocialLinksRowSpacing = SpaceSize.medium

@Composable
fun SocialButtons(
    facebookUrl: String?,
    instagramUrl: String?,
    youtubeUrl: String?,
    modifier: Modifier = Modifier,
) {
    val hasAnyLink = !facebookUrl.isNullOrBlank() || !instagramUrl.isNullOrBlank() || !youtubeUrl.isNullOrBlank()
    if (!hasAnyLink) return

    val openSocialLink = rememberSocialLinkOpener()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SocialLinksRowSpacing),
    ) {
        if (!facebookUrl.isNullOrBlank()) {
            SocialLinkIcon(
                icon = Res.drawable.facebook_icon,
                contentDescription = stringResource(Res.string.social_facebook_icon_description),
                onClick = { openSocialLink(SocialPlatform.FACEBOOK, facebookUrl) },
            )
        }
        if (!instagramUrl.isNullOrBlank()) {
            SocialLinkIcon(
                icon = Res.drawable.instagram_icon,
                contentDescription = stringResource(Res.string.social_instagram_icon_description),
                onClick = { openSocialLink(SocialPlatform.INSTAGRAM, instagramUrl) },
            )
        }
        if (!youtubeUrl.isNullOrBlank()) {
            SocialLinkIcon(
                icon = Res.drawable.youtube_icon,
                contentDescription = stringResource(Res.string.social_youtube_icon_description),
                onClick = { openSocialLink(SocialPlatform.YOUTUBE, youtubeUrl) },
            )
        }
    }
}

@Composable
private fun SocialLinkIcon(
    icon: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Image(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(SocialLinkIconSize)
            .clickable(onClick = onClick),
    )
}

@Preview
@Composable
private fun SocialButtonsPreview() {
    AppTheme {
        SocialButtons(
            facebookUrl = "https://facebook.com/johndoe",
            instagramUrl = "https://instagram.com/johndoe",
            youtubeUrl = "https://youtube.com/@johndoe",
        )
    }
}