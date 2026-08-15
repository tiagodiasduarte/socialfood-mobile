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
import socialfood.composeapp.generated.resources.edit_profile_email_label
import socialfood.composeapp.generated.resources.edit_profile_name_label
import socialfood.composeapp.generated.resources.edit_profile_name_placeholder
import socialfood.composeapp.generated.resources.edit_profile_personal_details_title
import socialfood.composeapp.generated.resources.edit_profile_username_label
import socialfood.composeapp.generated.resources.edit_profile_username_placeholder

@Composable
fun PersonalDetailsCard(
    state: EditProfileUiState.Loaded,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
) {
    SectionCard {
        Text(
            text = stringResource(Res.string.edit_profile_personal_details_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_name_label),
            value = state.name,
            placeholder = stringResource(Res.string.edit_profile_name_placeholder),
            onValueChange = onNameChange,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_email_label),
            value = state.email,
            placeholder = "",
            onValueChange = {},
            enabled = false,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_username_label),
            value = state.username,
            placeholder = stringResource(Res.string.edit_profile_username_placeholder),
            onValueChange = onUsernameChange,
        )
    }
}
