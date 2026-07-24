package pt.socialfood.presentation.components.buttons.social

import androidx.compose.runtime.Composable

enum class SocialPlatform { FACEBOOK, INSTAGRAM, YOUTUBE }

@Composable
expect fun rememberSocialLinkOpener(): (socialPlatform: SocialPlatform, url: String) -> Unit