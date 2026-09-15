package pt.socialfood.presentation.guide.shared.join

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.error.stringResource
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.join_guide_dialog_code_placeholder
import socialfood.composeapp.generated.resources.join_guide_dialog_subtitle
import socialfood.composeapp.generated.resources.join_guide_dialog_title
import socialfood.composeapp.generated.resources.join_shared_guide_screen_close_button_description
import socialfood.composeapp.generated.resources.join_shared_guide_screen_join_button

private const val JOIN_GUIDE_CODE_LENGTH = 8
private const val JOIN_GUIDE_CODE_ALLOWED_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinSharedGuideCodeInput(
    show: Boolean,
    state: JoinSharedGuideDialogUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!show) return

    var code by remember { mutableStateOf("") }
    val isLoading = state is JoinSharedGuideDialogUiState.Loading

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = SpaceSize.large, vertical = SpaceSize.medium),
        ) {
            JoinSharedGuideCodeHeader(onCloseClick = onDismiss)

            Spacer(Modifier.height(SpaceSize.large))

            JoinSharedGuideCodeField(
                code = code,
                onCodeChange = { code = it },
                state = state,
                isLoading = isLoading,
            )

            Spacer(Modifier.height(SpaceSize.large))

            Button(
                onClick = { onConfirm(code) },
                enabled = !isLoading && code.length == JOIN_GUIDE_CODE_LENGTH,
                shape = RoundedCornerShape(SpaceSize.large),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.join_shared_guide_screen_join_button))
                }
            }
        }
    }
}

@Composable
private fun JoinSharedGuideCodeHeader(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(
                text = stringResource(Res.string.join_guide_dialog_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(SpaceSize.small))

            Text(
                text = stringResource(Res.string.join_guide_dialog_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.join_shared_guide_screen_close_button_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun JoinSharedGuideCodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    state: JoinSharedGuideDialogUiState,
    isLoading: Boolean,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column {
        OutlinedTextField(
            value = code,
            onValueChange = { value ->
                val filtered = value.uppercase().filter { it in JOIN_GUIDE_CODE_ALLOWED_CHARS }
                if (filtered.length <= JOIN_GUIDE_CODE_LENGTH) onCodeChange(filtered)
            },
            placeholder = { Text(stringResource(Res.string.join_guide_dialog_code_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                )
            },
            singleLine = true,
            enabled = !isLoading,
            isError = state is JoinSharedGuideDialogUiState.Error,
            shape = RoundedCornerShape(SpaceSize.medium),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )

        if (state is JoinSharedGuideDialogUiState.Error) {
            Spacer(Modifier.height(SpaceSize.medium))

            Text(
                text = stringResource(state.errorCode.stringResource()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
