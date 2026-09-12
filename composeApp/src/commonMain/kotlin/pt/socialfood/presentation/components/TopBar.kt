package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description

@Suppress("LongParameterList")
@Composable
fun TopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showActionButton: Boolean = true,
    isActionLoading: Boolean = false,
    actionButtonText: String = "",
    onActionClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.back_button_description),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        if (showActionButton) {
            Button(
                onClick = onActionClick,
                enabled = !isActionLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(SpaceSize.large),
            ) {
                if (isActionLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(SpaceSize.large),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = actionButtonText,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        } else {
            Box(modifier = Modifier.size(48.dp))
        }
    }
}

@Preview
@Composable
private fun TopBarWithActionPreview() {
    AppTheme {
        TopBar(
            title = "Edit Profile",
            onBackClick = {},
            actionButtonText = "Save",
            onActionClick = {},
        )
    }
}

@Preview
@Composable
private fun TopBarLoadingPreview() {
    AppTheme {
        TopBar(
            title = "Edit Profile",
            onBackClick = {},
            isActionLoading = true,
            actionButtonText = "Save",
            onActionClick = {},
        )
    }
}

@Preview
@Composable
private fun TopBarWithoutActionPreview() {
    AppTheme {
        TopBar(
            title = "Edit Profile",
            onBackClick = {},
            showActionButton = false,
        )
    }
}
