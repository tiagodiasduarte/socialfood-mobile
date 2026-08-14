package pt.socialfood.presentation.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_validation_error_dialog_ok
import socialfood.composeapp.generated.resources.edit_guide_validation_error_dialog_title

@Composable
fun GuideValidationErrorDialog(
    errors: List<StringResource>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.edit_guide_validation_error_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.small)) {
                errors.forEach { error ->
                    Text(
                        text = "• ${stringResource(error)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.edit_guide_validation_error_dialog_ok))
            }
        },
    )
}
