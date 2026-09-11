package pt.socialfood.presentation.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.profile_logout_confirmation_cancel
import socialfood.composeapp.generated.resources.profile_logout_confirmation_confirm
import socialfood.composeapp.generated.resources.profile_logout_confirmation_message
import socialfood.composeapp.generated.resources.profile_logout_confirmation_title

// Swift side must implement this interface and assign it to LogoutConfirmationBridge.shared.delegate.
// See AlertDialogDelegateImpl.swift for the UIAlertController-based implementation.

interface LogoutConfirmationDelegate {
    fun showConfirmation(
        title: String,
        message: String,
        confirmLabel: String,
        cancelLabel: String,
        onConfirm: () -> Unit,
    )
}

object LogoutConfirmationBridge {
    var delegate: LogoutConfirmationDelegate? = null
}

@Composable
actual fun rememberLogoutConfirmationLauncher(onConfirm: () -> Unit): () -> Unit {
    val title = stringResource(Res.string.profile_logout_confirmation_title)
    val message = stringResource(Res.string.profile_logout_confirmation_message)
    val confirmLabel = stringResource(Res.string.profile_logout_confirmation_confirm)
    val cancelLabel = stringResource(Res.string.profile_logout_confirmation_cancel)

    return remember(title, message, confirmLabel, cancelLabel, onConfirm) {
        {
            LogoutConfirmationBridge.delegate?.showConfirmation(
                title = title,
                message = message,
                confirmLabel = confirmLabel,
                cancelLabel = cancelLabel,
                onConfirm = onConfirm,
            )
        }
    }
}
