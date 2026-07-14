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
import socialfood.composeapp.generated.resources.edit_profile_city_label
import socialfood.composeapp.generated.resources.edit_profile_city_placeholder
import socialfood.composeapp.generated.resources.edit_profile_country_label
import socialfood.composeapp.generated.resources.edit_profile_country_placeholder
import socialfood.composeapp.generated.resources.edit_profile_first_name_label
import socialfood.composeapp.generated.resources.edit_profile_first_name_placeholder
import socialfood.composeapp.generated.resources.edit_profile_last_name_label
import socialfood.composeapp.generated.resources.edit_profile_last_name_placeholder
import socialfood.composeapp.generated.resources.edit_profile_personal_details_title
import socialfood.composeapp.generated.resources.edit_profile_phone_label
import socialfood.composeapp.generated.resources.edit_profile_phone_placeholder
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.presentation.profile.edit.EditProfileUiState
import pt.socialfood.presentation.profile.edit.ProfileTextField
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun PersonalDetailsCard(
    state: EditProfileUiState.Loaded,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
) {
    SectionCard {
        Text(
            text = stringResource(Res.string.edit_profile_personal_details_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_first_name_label),
            value = state.firstName,
            placeholder = stringResource(Res.string.edit_profile_first_name_placeholder),
            onValueChange = onFirstNameChange,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_last_name_label),
            value = state.lastName,
            placeholder = stringResource(Res.string.edit_profile_last_name_placeholder),
            onValueChange = onLastNameChange,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_phone_label),
            value = state.phoneNumber,
            placeholder = stringResource(Res.string.edit_profile_phone_placeholder),
            onValueChange = onPhoneNumberChange,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_city_label),
            value = state.city,
            placeholder = stringResource(Res.string.edit_profile_city_placeholder),
            onValueChange = onCityChange,
        )

        Spacer(Modifier.height(SpaceSize.large))

        ProfileTextField(
            label = stringResource(Res.string.edit_profile_country_label),
            value = state.country,
            placeholder = stringResource(Res.string.edit_profile_country_placeholder),
            onValueChange = onCountryChange,
        )
    }
}