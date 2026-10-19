package pt.socialfood.feature.profile.edit.card

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.core.designsystem.component.card.SectionCard
import pt.socialfood.core.designsystem.theme.AppTheme
import pt.socialfood.core.designsystem.theme.SpaceSize
import pt.socialfood.feature.profile.edit.EditProfileUiState
import pt.socialfood.feature.profile.edit.ProfileTextField
import pt.socialfood.feature.profile.impl.generated.resources.Res
import pt.socialfood.feature.profile.impl.generated.resources.edit_profile_email_label
import pt.socialfood.feature.profile.impl.generated.resources.edit_profile_name_label
import pt.socialfood.feature.profile.impl.generated.resources.edit_profile_name_placeholder
import pt.socialfood.feature.profile.impl.generated.resources.edit_profile_personal_details_title
import pt.socialfood.feature.profile.impl.generated.resources.edit_profile_username_label
import pt.socialfood.feature.profile.impl.generated.resources.edit_profile_username_placeholder

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
            label = stringResource(Res.string.edit_profile_email_label),
            value = state.email,
            placeholder = "",
            onValueChange = {},
            enabled = false,
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
            label = stringResource(Res.string.edit_profile_username_label),
            value = state.username,
            placeholder = stringResource(Res.string.edit_profile_username_placeholder),
            onValueChange = onUsernameChange,
        )
    }
}

@Preview
@Composable
private fun PersonalDetailsCardPreview() {
    AppTheme {
        PersonalDetailsCard(
            state = EditProfileUiState.Loaded(
                name = "John Doe",
                email = "john.doe@email.com",
                username = "johndoe",
            ),
            onNameChange = {},
            onUsernameChange = {},
        )
    }
}
