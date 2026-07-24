package pt.socialfood.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.profile_contact_card_title_label
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun ContactCard(user: User) {
    SectionCard {
        Column(modifier = Modifier.fillMaxWidth().padding(SpaceSize.large)) {
            Text(
                text = stringResource(Res.string.profile_contact_card_title_label),
                style = AppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = SpaceSize.large),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.large)) {
                ContactRow(icon = Icons.Outlined.Email, text = user.email)
                user.address?.takeIf { it.isNotBlank() }?.let {
                    ContactRow(Icons.Outlined.LocationOn, it)
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}