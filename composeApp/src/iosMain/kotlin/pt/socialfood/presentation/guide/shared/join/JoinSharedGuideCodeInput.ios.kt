package pt.socialfood.presentation.guide.shared.join

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.error.stringResource

// Swift side must implement this interface and assign it to JoinSharedGuideCodeBridge.shared.delegate.
// See JoinSharedGuideCodeDelegateImpl.swift for the UIAlertController-based implementation.
interface JoinSharedGuideCodeDelegate {
    fun presentCodeInput(
        prefillCode: String?,
        errorMessage: String?,
        onConfirm: (code: String) -> Unit,
        onCancel: () -> Unit,
    )
}

object JoinSharedGuideCodeBridge {
    var delegate: JoinSharedGuideCodeDelegate? = null
}

@Composable
actual fun JoinSharedGuideCodeInput(
    show: Boolean,
    state: JoinSharedGuideUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var lastCode by remember { mutableStateOf<String?>(null) }
    val errorMessage = (state as? JoinSharedGuideUiState.Error)?.let { stringResource(it.errorCode.stringResource()) }

    fun present(errorMessage: String?) {
        val delegate = JoinSharedGuideCodeBridge.delegate
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
