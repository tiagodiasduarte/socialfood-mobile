package pt.socialfood.presentation.guide.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.error.stringResource

// Swift side must implement this interface and assign it to JoinGuideCodeBridge.shared.delegate.
// See JoinGuideCodeDelegateImpl.swift for the UIAlertController-based implementation.
interface JoinGuideCodeDelegate {
    fun presentCodeInput(onConfirm: (code: String) -> Unit, onCancel: () -> Unit)
    fun presentError(message: String)
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
    val errorMessage = (state as? JoinGuideUiState.Error)?.let { stringResource(it.errorCode.stringResource()) }

    LaunchedEffect(show) {
        if (show) {
            val delegate = JoinGuideCodeBridge.delegate
            if (delegate != null) {
                delegate.presentCodeInput(onConfirm = onConfirm, onCancel = onDismiss)
            } else {
                onDismiss()
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { JoinGuideCodeBridge.delegate?.presentError(it) }
    }
}
