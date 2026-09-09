import ComposeApp
import UIKit

class GuideValidationErrorDialogDelegateImpl: GuideValidationErrorDialogDelegate {
    func showError(title: String, message: String, okLabel: String, onDismiss: @escaping () -> Void) {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = scene.windows.first?.rootViewController else {
            onDismiss()
            return
        }

        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: okLabel, style: .default) { _ in onDismiss() })
        rootVC.present(alert, animated: true)
    }
}
