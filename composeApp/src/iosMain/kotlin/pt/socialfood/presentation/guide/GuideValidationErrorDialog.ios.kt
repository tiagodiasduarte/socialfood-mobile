package pt.socialfood.presentation.guide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_validation_error_dialog_ok
import socialfood.composeapp.generated.resources.edit_guide_validation_error_dialog_title

// Swift side must implement this interface and assign it to GuideValidationErrorDialogBridge.shared.delegate.
// See AlertDialogDelegateImpl.swift for the UIAlertController-based implementation pattern.
interface GuideValidationErrorDialogDelegate {
    fun showError(title: String, message: String, okLabel: String, onDismiss: () -> Unit)
}

object GuideValidationErrorDialogBridge {
    var delegate: GuideValidationErrorDialogDelegate? = null
}

@Composable
actual fun GuideValidationErrorDialog(errors: List<StringResource>, onDismiss: () -> Unit) {
    val title = stringResource(Res.string.edit_guide_validation_error_dialog_title)
    val okLabel = stringResource(Res.string.edit_guide_validation_error_dialog_ok)
    val message = buildString {
        errors.forEachIndexed { index, error ->
            if (index > 0) appendLine()
            append("• ${stringResource(error)}")
        }
    }

    LaunchedEffect(errors) {
        val delegate = GuideValidationErrorDialogBridge.delegate
        if (delegate != null) {
            delegate.showError(title, message, okLabel, onDismiss)
        } else {
            onDismiss()
        }
    }
}
