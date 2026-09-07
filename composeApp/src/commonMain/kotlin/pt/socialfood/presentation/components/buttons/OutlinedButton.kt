package pt.socialfood.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

private val BorderWidth = 1.dp
private val IconSize = 20.dp

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(SpaceSize.medium))
            .border(BorderWidth, color, RoundedCornerShape(SpaceSize.medium))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(IconSize),
            )
        }
        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
            )
        }
    }
}

@Preview
@Composable
private fun OutlinedButtonIconAndTextPreview() {
    AppTheme {
        OutlinedButton(
            text = "Map",
            icon = Icons.Outlined.Map,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun OutlinedButtonIconOnlyPreview() {
    AppTheme {
        OutlinedButton(
            icon = Icons.Outlined.Map,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun OutlinedButtonTextOnlyPreview() {
    AppTheme {
        OutlinedButton(
            text = "Map",
            onClick = {},
        )
    }
}
