package pt.socialfood.presentation.components.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun FilledButton(onClick: () -> Unit, modifier: Modifier = Modifier, text: String? = null, icon: ImageVector? = null) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(SpaceSize.large),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            if (text != null) {
                Spacer(Modifier.width(SpaceSize.small))
            }
        }
        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = SpaceSize.small),
            )
        }
    }
}

@Preview
@Composable
private fun FilledButtonIconAndTextPreview() {
    AppTheme {
        FilledButton(
            text = "Try Again",
            icon = Icons.Outlined.Refresh,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun FilledButtonIconOnlyPreview() {
    AppTheme {
        FilledButton(
            icon = Icons.Outlined.Refresh,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun FilledButtonTextOnlyPreview() {
    AppTheme {
        FilledButton(
            text = "Try Again",
            onClick = {},
        )
    }
}
