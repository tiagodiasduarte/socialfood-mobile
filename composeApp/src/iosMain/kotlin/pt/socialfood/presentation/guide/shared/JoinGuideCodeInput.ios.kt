package pt.socialfood.presentation.guide.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.error.stringResource

// Swift side must implement this interface and assign it to JoinGuideCodeBridge.shared.delegate.
// See JoinGuideCodeDelegateImpl.swift for the UIAlertController-based implementation.
interface JoinGuideCodeDelegate {
    fun presentCodeInput(
        prefillCode: String?,
        errorMessage: String?,
        onConfirm: (code: String) -> Unit,
        onCancel: () -> Unit,
    )
}

object JoinGuideCodeBridge {
    var delegate: JoinGuideCodeDelegate? = null
}

@Composable
actual fun JoinGuideCodeInput(
    show: Boolean,
    state: JoinGuideUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var lastCode by remember { mutableStateOf<String?>(null) }
    val errorMessage = (state as? JoinGuideUiState.Error)?.let { stringResource(it.errorCode.stringResource()) }

    fun present(errorMessage: String?) {
        val delegate = JoinGuideCodeBridge.delegate
        if (delegate != null) {
            delegate.presentCodeInput(
                prefillCode = lastCode,
                errorMessage = errorMessage,
                onConfirm = { code ->
                    lastCode = code
                    onConfirm(code)
                },
                onCancel = onDismiss,
            )
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(show) {
        if (show) {
            lastCode = null
            present(errorMessage = null)
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) present(errorMessage = errorMessage)
    }
}
