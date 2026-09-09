package pt.socialfood.presentation.guide.shared.join

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.error.stringResource
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.join_guide_dialog_cancel_button
import socialfood.composeapp.generated.resources.join_guide_dialog_code_label
import socialfood.composeapp.generated.resources.join_guide_dialog_code_length_message
import socialfood.composeapp.generated.resources.join_guide_dialog_code_placeholder
import socialfood.composeapp.generated.resources.join_guide_dialog_confirm_button
import socialfood.composeapp.generated.resources.join_guide_dialog_title

private const val JOIN_GUIDE_CODE_LENGTH = 8
private const val JOIN_GUIDE_CODE_ALLOWED_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

@Composable
fun JoinSharedGuideDialog(state: JoinSharedGuideUiState, onConfirm: (code: String) -> Unit, onDismiss: () -> Unit) {
    var code by remember { mutableStateOf("") }
    val isLoading = state is JoinSharedGuideUiState.Loading

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.join_guide_dialog_title)) },
        text = {
            JoinSharedGuideCodeField(
                code = code,
                onCodeChange = { code = it },
                state = state,
                isLoading = isLoading,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(code) },
                enabled = !isLoading && code.length == JOIN_GUIDE_CODE_LENGTH,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(Res.string.join_guide_dialog_confirm_button))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(stringResource(Res.string.join_guide_dialog_cancel_button))
            }
        },
    )
}

@Composable
private fun JoinSharedGuideCodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    state: JoinSharedGuideUiState,
    isLoading: Boolean,
) {
    Column {
        OutlinedTextField(
            value = code,
            onValueChange = { value ->
                val filtered = value.uppercase().filter { it in JOIN_GUIDE_CODE_ALLOWED_CHARS }
                if (filtered.length <= JOIN_GUIDE_CODE_LENGTH) onCodeChange(filtered)
            },
            label = { Text(stringResource(Res.string.join_guide_dialog_code_label)) },
            placeholder = { Text(stringResource(Res.string.join_guide_dialog_code_placeholder)) },
            singleLine = true,
            enabled = !isLoading,
            isError = state is JoinSharedGuideUiState.Error,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = stringResource(Res.string.join_guide_dialog_code_length_message),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = SpaceSize.small),
        )

        if (state is JoinSharedGuideUiState.Error) {
            Spacer(Modifier.height(SpaceSize.medium))

            Text(
                text = stringResource(state.errorCode.stringResource()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = SpaceSize.small),
            )
        }
    }
}
