package pt.socialfood.presentation.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.create_guide_save_button

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(SpaceSize.large),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = stringResource(Res.string.create_guide_save_button),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = SpaceSize.small),
            )
        }
    }
}

@Preview
@Composable
private fun SaveButtonPreview() {
    AppTheme {
        SaveButton(onClick = {})
    }
}

@Preview
@Composable
private fun SaveButtonLoadingPreview() {
    AppTheme {
        SaveButton(onClick = {}, isLoading = true)
    }
}
