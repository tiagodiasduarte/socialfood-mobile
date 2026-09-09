import ComposeApp
import UIKit

class DeleteGuideConfirmationDelegateImpl: DeleteGuideConfirmationDelegate {
    func showConfirmation(
        title: String,
        message: String,
        confirmLabel: String,
        cancelLabel: String,
        onConfirm: @escaping () -> Void
    ) {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = scene.windows.first?.rootViewController else {
            return
        }

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: cancelLabel, style: .cancel))
        alert.addAction(UIAlertAction(title: confirmLabel, style: .destructive) { _ in onConfirm() })
        rootVC.present(alert, animated: true)
    }
}
