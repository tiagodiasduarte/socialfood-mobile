import ComposeApp
import UIKit

private let joinGuideCodeLength = 8
private let joinGuideCodeAllowedCharacters = CharacterSet(charactersIn: "ABCDEFGHJKLMNPQRSTUVWXYZ23456789")
private let joinGuideCodeDefaultMessage = "Enter the 8-character invite code"

class JoinGuideCodeDelegateImpl: NSObject, JoinGuideCodeDelegate, UITextFieldDelegate {
    func presentCodeInput(
        prefillCode: String?,
        errorMessage: String?,
        onConfirm: @escaping (String) -> Void,
        onCancel: @escaping () -> Void
    ) {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = scene.windows.first?.rootViewController else {
            onCancel()
            return
        }

        let alert = UIAlertController(
            title: "Join a guide",
            message: errorMessage ?? joinGuideCodeDefaultMessage,
            preferredStyle: .alert
        )
        alert.addTextField { textField in
            textField.placeholder = "Invite code"
            textField.text = prefillCode
            textField.autocapitalizationType = .allCharacters
            textField.autocorrectionType = .no
            textField.delegate = self
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel) { _ in onCancel() })
        alert.addAction(UIAlertAction(title: "Join", style: .default) { [weak alert] _ in
            let code = alert?.textFields?.first?.text ?? ""
            onConfirm(code)
        })
        rootVC.present(alert, animated: true)
    }

    func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        let current = (textField.text as NSString?) ?? ""
        let updated = current.replacingCharacters(in: range, with: string).uppercased()
        let filtered = String(updated.unicodeScalars.filter { joinGuideCodeAllowedCharacters.contains($0) })
        textField.text = String(filtered.prefix(joinGuideCodeLength))
        return false
    }
}
