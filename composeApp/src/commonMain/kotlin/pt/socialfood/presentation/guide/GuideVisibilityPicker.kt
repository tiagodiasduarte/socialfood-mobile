package pt.socialfood.presentation.guide

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_icon
import socialfood.composeapp.generated.resources.edit_guide_visibility_label
import socialfood.composeapp.generated.resources.edit_guide_visibility_private
import socialfood.composeapp.generated.resources.edit_guide_visibility_private_description
import socialfood.composeapp.generated.resources.edit_guide_visibility_public
import socialfood.composeapp.generated.resources.edit_guide_visibility_public_description
import socialfood.composeapp.generated.resources.edit_guide_visibility_shared
import socialfood.composeapp.generated.resources.edit_guide_visibility_shared_description
import socialfood.composeapp.generated.resources.guide_private_icon
import socialfood.composeapp.generated.resources.guide_public_icon

private data class VisibilityOption(
    val visibility: GuideVisibility,
    val label: StringResource,
    val description: StringResource,
    val icon: DrawableResource,
)

private val visibilityOptions = listOf(
    VisibilityOption(
        GuideVisibility.PUBLIC,
        Res.string.edit_guide_visibility_public,
        Res.string.edit_guide_visibility_public_description,
        Res.drawable.guide_public_icon,
    ),
    VisibilityOption(
        GuideVisibility.PRIVATE,
        Res.string.edit_guide_visibility_private,
        Res.string.edit_guide_visibility_private_description,
        Res.drawable.guide_private_icon,
    ),
    VisibilityOption(
        GuideVisibility.SHARED,
        Res.string.edit_guide_visibility_shared,
        Res.string.edit_guide_visibility_shared_description,
        Res.drawable.author_icon,
    ),
)

@Composable
fun GuideVisibilityPicker(
    visibility: GuideVisibility,
    onVisibilityChange: (GuideVisibility) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
        Text(
            text = stringResource(Res.string.edit_guide_visibility_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            visibilityOptions.forEach { option ->
                VisibilityButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(option.label),
                    icon = option.icon,
                    isSelected = visibility == option.visibility,
                    onClick = { onVisibilityChange(option.visibility) },
                )
            }
        }

        val selectedOption = visibilityOptions.first { it.visibility == visibility }

        Row(
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(selectedOption.description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun VisibilityButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: DrawableResource,
    isSelected: Boolean,
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
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(textColor),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 11.sp,
                    maxFontSize = MaterialTheme.typography.labelLarge.fontSize,
                ),
            )
        }
    }
}

@Composable
@Preview
private fun GuideVisibilityPickerPreview() {
    AppTheme {
        GuideVisibilityPicker(
            visibility = GuideVisibility.PUBLIC,
            onVisibilityChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSize.large),
        )
    }
}
