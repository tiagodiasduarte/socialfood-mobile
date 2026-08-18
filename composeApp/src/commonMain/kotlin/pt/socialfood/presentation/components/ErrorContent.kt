package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.buttons.IconTextButton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.error_content_description
import socialfood.composeapp.generated.resources.error_content_error_code_label
import socialfood.composeapp.generated.resources.error_content_retry_button
import socialfood.composeapp.generated.resources.error_content_subtitle
import socialfood.composeapp.generated.resources.error_content_title
import socialfood.composeapp.generated.resources.server_error_icon

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    errorCode: String? = null,
    backgroundColor: Color = GreyBackground,
    onRetryClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(backgroundColor)
            .padding(SpaceSize.xlarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.server_error_icon),
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.height(SpaceSize.xlarge))

        Text(
            text = stringResource(Res.string.error_content_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(SpaceSize.medium))

        Text(
            text = stringResource(Res.string.error_content_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(SpaceSize.small))

        Text(
            text = stringResource(Res.string.error_content_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(SpaceSize.xlarge))

        IconTextButton(
            text = stringResource(Res.string.error_content_retry_button),
            icon = Icons.Outlined.Refresh,
            onClick = onRetryClick,
        )

        if (errorCode != null) {
            Spacer(Modifier.height(SpaceSize.large))

            Text(
                text = stringResource(Res.string.error_content_error_code_label, errorCode),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun ErrorContentPreview() {
    AppTheme {
        ErrorContent(errorCode = "500 • Internal Server Error")
    }
}
