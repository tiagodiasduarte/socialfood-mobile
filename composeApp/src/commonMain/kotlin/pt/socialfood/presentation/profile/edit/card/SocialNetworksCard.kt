package pt.socialfood.presentation.profile.edit.card

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.presentation.profile.edit.EditProfileUiState
import pt.socialfood.presentation.profile.edit.ProfileTextField
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_profile_facebook_label
import socialfood.composeapp.generated.resources.edit_profile_facebook_placeholder
import socialfood.composeapp.generated.resources.edit_profile_instagram_label
import socialfood.composeapp.generated.resources.edit_profile_instagram_placeholder
import socialfood.composeapp.generated.resources.edit_profile_social_title
import socialfood.composeapp.generated.resources.edit_profile_youtube_label
import socialfood.composeapp.generated.resources.edit_profile_youtube_placeholder

@Composable
fun SocialNetworkCard(
    state: EditProfileUiState.Loaded,
    onFacebookUrlChange: (String) -> Unit,
    onInstagramUrlChange: (String) -> Unit,
    onYoutubeUrlChange: (String) -> Unit,
) {
    SectionCard {
        Text(
            text = stringResource(Res.string.edit_profile_social_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_facebook_label),
            value = state.facebookUrl,
            placeholder = stringResource(Res.string.edit_profile_facebook_placeholder),
            onValueChange = onFacebookUrlChange,
            clearable = true,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_instagram_label),
            value = state.instagramUrl,
            placeholder = stringResource(Res.string.edit_profile_instagram_placeholder),
            onValueChange = onInstagramUrlChange,
            clearable = true,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_youtube_label),
            value = state.youtubeUrl,
            placeholder = stringResource(Res.string.edit_profile_youtube_placeholder),
            onValueChange = onYoutubeUrlChange,
            clearable = true,
        )
    }
}
