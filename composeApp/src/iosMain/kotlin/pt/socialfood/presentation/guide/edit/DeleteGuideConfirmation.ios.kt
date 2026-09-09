package pt.socialfood.presentation.guide.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_cancel
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_confirm
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_message
import socialfood.composeapp.generated.resources.edit_guide_delete_confirmation_title

// Swift side must implement this interface and assign it to DeleteGuideConfirmationBridge.delegate.
// Example Swift implementation using UIAlertController:
//
//   class DeleteGuideConfirmationDelegateImpl: NSObject, DeleteGuideConfirmationDelegate {
//
//     var rootViewController: UIViewController
//     init(rootViewController: UIViewController) { self.rootViewController = rootViewController }
//
//     func showConfirmation(
//       title: String,
//       message: String,
//       confirmLabel: String,
//       cancelLabel: String,
//       onConfirm: @escaping () -> Void
//     ) {
//       let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
//       alert.addAction(UIAlertAction(title: cancelLabel, style: .cancel))
//       alert.addAction(UIAlertAction(title: confirmLabel, style: .destructive) { _ in onConfirm() })
//       rootViewController.present(alert, animated: true)
//     }
//   }
//
//   // In your iOS app entry point:
//   DeleteGuideConfirmationBridge.shared.delegate =
//       DeleteGuideConfirmationDelegateImpl(rootViewController: window.rootViewController!)

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
