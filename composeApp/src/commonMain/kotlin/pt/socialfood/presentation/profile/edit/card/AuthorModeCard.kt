package pt.socialfood.presentation.profile.edit.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.presentation.profile.edit.EditProfileUiState
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_profile_author_mode_description
import socialfood.composeapp.generated.resources.edit_profile_author_mode_title

@Composable
fun AuthorModeCard(state: EditProfileUiState.Loaded, onAuthorModeChange: (Boolean) -> Unit) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.edit_profile_author_mode_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(Res.string.edit_profile_author_mode_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.width(SpaceSize.medium))

            Switch(
                checked = state.isAuthor,
                onCheckedChange = onAuthorModeChange,
            )
        }
    }
}
