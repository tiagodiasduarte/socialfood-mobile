package pt.socialfood.presentation.guide.edit.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.buttons.OutlinedButton
import pt.socialfood.presentation.guide.GuideVisibilityPicker
import pt.socialfood.presentation.guide.edit.rememberDeleteGuideConfirmationLauncher
import pt.socialfood.ui.theme.SpaceSize
import pt.socialfood.ui.theme.StatusGuide
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_delete_button
import socialfood.composeapp.generated.resources.edit_guide_details_public_author_warning
import socialfood.composeapp.generated.resources.edit_guide_details_public_image_warning
import socialfood.composeapp.generated.resources.edit_guide_needs_restaurants_warning
import socialfood.composeapp.generated.resources.edit_guide_publication_rules_label
import socialfood.composeapp.generated.resources.edit_guide_share_code_copy_action
import socialfood.composeapp.generated.resources.edit_guide_share_code_section_label

private const val MIN_RESTAURANTS = 3
private const val SHARE_CODE_PLACEHOLDER = "_ _ _ _ _ _"

@Composable
fun GuideStatusCard(
    visibility: GuideVisibility,
    restaurantCount: Int,
    hasImage: Boolean,
    isAuthor: Boolean,
    onVisibilityChange: (GuideVisibility) -> Unit,
    onDeleteGuide: () -> Unit,
    modifier: Modifier = Modifier,
    shareCode: String? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            GuideVisibilityPicker(visibility = visibility, onVisibilityChange = onVisibilityChange)

            if (visibility == GuideVisibility.PUBLIC) {
                Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                    Text(
                        text = stringResource(Res.string.edit_guide_publication_rules_label),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    PublicationRuleRow(
                        met = restaurantCount >= MIN_RESTAURANTS,
                        label = stringResource(Res.string.edit_guide_needs_restaurants_warning),
                    )
                    PublicationRuleRow(
                        met = hasImage,
                        label = stringResource(Res.string.edit_guide_details_public_image_warning),
                    )
                    PublicationRuleRow(
                        met = isAuthor,
                        label = stringResource(Res.string.edit_guide_details_public_author_warning),
                    )
                }
            }

            if (visibility == GuideVisibility.SHARED) {
                GuideShareCodeSection(shareCode = shareCode ?: SHARE_CODE_PLACEHOLDER)
            }

            val launchDeleteConfirmation = rememberDeleteGuideConfirmationLauncher(onConfirm = onDeleteGuide)
            OutlinedButton(
                modifier = Modifier
                    .padding(top = SpaceSize.large)
                    .height(40.dp)
                    .fillMaxWidth(),
                text = stringResource(Res.string.edit_guide_delete_button),
                icon = Icons.Outlined.Delete,
                color = Color.Red,
                onClick = launchDeleteConfirmation,
            )
        }
    }
}

@Composable
private fun GuideShareCodeSection(shareCode: String) {
    val clipboardManager = LocalClipboardManager.current
    val copyActionLabel = stringResource(Res.string.edit_guide_share_code_copy_action)

    Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
        Text(
            text = stringResource(Res.string.edit_guide_share_code_section_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(SpaceSize.medium))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .clickable { clipboardManager.setText(AnnotatedString(shareCode)) }
                .padding(
                    start = SpaceSize.large,
                    end = SpaceSize.medium,
                    top = SpaceSize.small,
                    bottom = SpaceSize.small,
                ),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = shareCode,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = copyActionLabel,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun PublicationRuleRow(met: Boolean, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = SpaceSize.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        Icon(
            imageVector = if (met) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (met) StatusGuide else Color.Red,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (met) StatusGuide else Color.Red,
        )
    }
}
