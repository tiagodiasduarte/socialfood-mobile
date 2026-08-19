import ComposeApp
import GoogleSignIn
import UIKit

class GoogleSignInDelegateImpl: GoogleSignInDelegate {
    func signIn(onIdToken: @escaping (String) -> Void, onError: @escaping (String) -> Void) {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = scene.windows.first?.rootViewController else {
            onError("Could not find root view controller")
            return
        }
        GIDSignIn.sharedInstance.signIn(withPresenting: rootVC) { result, error in
            if let idToken = result?.user.idToken?.tokenString {
                onIdToken(idToken)
            } else {
                onError(error?.localizedDescription ?? "Unknown error")
            }
        }
    }
}
