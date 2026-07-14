package pt.socialfood.presentation.profile.edit.card

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_profile_about_title
import socialfood.composeapp.generated.resources.edit_profile_bio_label
import socialfood.composeapp.generated.resources.edit_profile_bio_placeholder
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.presentation.profile.edit.EditProfileUiState
import pt.socialfood.presentation.profile.edit.ProfileTextField
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun BioCard(
    state: EditProfileUiState.Loaded,
    onBioChange: (String) -> Unit,
) {
    SectionCard {
        Text(
            text = stringResource(Res.string.edit_profile_about_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_bio_label),
            value = state.bio,
            placeholder = stringResource(Res.string.edit_profile_bio_placeholder),
            onValueChange = onBioChange,
        )
    }
}