package pt.socialfood.presentation.guide.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_cancel
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_confirm
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_message
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_title

// Swift side must implement this interface and assign it to DeleteGuideConfirmationBridge.shared.delegate.
// See DeleteGuideConfirmationDelegateImpl.swift for the UIAlertController-based implementation.

interface DeleteGuideConfirmationDelegate {
    fun showConfirmation(
        title: String,
        message: String,
        confirmLabel: String,
        cancelLabel: String,
        onConfirm: () -> Unit,
    )
}

object DeleteGuideConfirmationBridge {
    var delegate: DeleteGuideConfirmationDelegate? = null
}

@Composable
actual fun rememberDeleteGuideConfirmationLauncher(onConfirm: () -> Unit): () -> Unit {
    val title = stringResource(Res.string.edit_guide_delete_confirmation_title)
    val message = stringResource(Res.string.edit_guide_delete_confirmation_message)
    val confirmLabel = stringResource(Res.string.edit_guide_delete_confirmation_confirm)
    val cancelLabel = stringResource(Res.string.edit_guide_delete_confirmation_cancel)

    return remember(title, message, confirmLabel, cancelLabel, onConfirm) {
        {
            DeleteGuideConfirmationBridge.delegate?.showConfirmation(
                title = title,
                message = message,
                confirmLabel = confirmLabel,
                cancelLabel = cancelLabel,
                onConfirm = onConfirm,
            )
        }
    }
}
