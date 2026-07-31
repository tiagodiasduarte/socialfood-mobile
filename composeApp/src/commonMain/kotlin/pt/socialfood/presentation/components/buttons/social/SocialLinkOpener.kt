package pt.socialfood.presentation.components.buttons.social

import androidx.compose.runtime.Composable

@Composable
expect fun rememberSocialLinkOpener(): (socialPlatform: SocialPlatform, url: String) -> Unit

enum class SocialPlatform { FACEBOOK, INSTAGRAM, YOUTUBE }