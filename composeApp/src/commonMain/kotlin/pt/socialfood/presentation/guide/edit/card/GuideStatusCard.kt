package pt.socialfood.presentation.guide.edit.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.ui.theme.SpaceSize
import pt.socialfood.ui.theme.StatusGuide
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_details_public_image_warning
import socialfood.composeapp.generated.resources.edit_guide_needs_restaurants_warning
import socialfood.composeapp.generated.resources.edit_guide_publication_rules_label
import socialfood.composeapp.generated.resources.edit_guide_visibility_label
import socialfood.composeapp.generated.resources.edit_guide_visibility_private
import socialfood.composeapp.generated.resources.edit_guide_visibility_public
import socialfood.composeapp.generated.resources.guides_private_icon
import socialfood.composeapp.generated.resources.guides_public_icon

private const val MIN_RESTAURANTS = 3

@Composable
fun GuideStatusCard(
    visibility: GuideVisibility,
    restaurantCount: Int,
    hasImage: Boolean,
    onVisibilityChange: (GuideVisibility) -> Unit,
    modifier: Modifier = Modifier,
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
            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                Text(
                    text = stringResource(Res.string.edit_guide_visibility_label),
                    style = MaterialTheme.typography.titleSmall,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                ) {
                    VisibilityButton(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.edit_guide_visibility_public),
                        isSelected = visibility == GuideVisibility.PUBLIC,
                        onClick = { onVisibilityChange(GuideVisibility.PUBLIC) },
                        icon = {
                            Image(
                                painter = painterResource(Res.drawable.guides_public_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(
                                    if (visibility == GuideVisibility.PUBLIC) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                ),
                            )
                        },
                    )
                    VisibilityButton(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.edit_guide_visibility_private),
                        isSelected = visibility == GuideVisibility.PRIVATE,
                        onClick = { onVisibilityChange(GuideVisibility.PRIVATE) },
                        icon = {
                            Image(
                                painter = painterResource(Res.drawable.guides_private_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(
                                    if (visibility == GuideVisibility.PRIVATE) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                ),
                            )
                        },
                    )
                }
            }

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
                }
            }
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

@Composable
private fun VisibilityButton(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val borderColor = if (isSelected) primary else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) primary else MaterialTheme.colorScheme.onSurfaceVariant

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
    ) {
        icon()
        Spacer(Modifier.width(SpaceSize.medium))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
        )
    }
}
