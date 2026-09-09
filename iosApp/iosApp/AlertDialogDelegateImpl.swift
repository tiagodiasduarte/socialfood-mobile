import ComposeApp
import UIKit

class AlertDialogDelegateImpl: GuideValidationErrorDialogDelegate, DeleteGuideConfirmationDelegate {
    func showError(title: String, message: String, okLabel: String, onDismiss: @escaping () -> Void) {
        guard let rootVC = rootViewController() else {
            onDismiss()
            return
        }

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: okLabel, style: .default) { _ in onDismiss() })
        rootVC.present(alert, animated: true)
    }

    func showConfirmation(
        title: String,
        message: String,
        confirmLabel: String,
        cancelLabel: String,
        onConfirm: @escaping () -> Void
    ) {
        guard let rootVC = rootViewController() else {
            return
        }

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: cancelLabel, style: .cancel))
        alert.addAction(UIAlertAction(title: confirmLabel, style: .destructive) { _ in onConfirm() })
        rootVC.present(alert, animated: true)
    }

    private func rootViewController() -> UIViewController? {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {
            return nil
        }
        return scene.windows.first?.rootViewController
    }
}
