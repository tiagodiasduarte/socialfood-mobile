import SwiftUI
import ComposeApp
import FirebaseCore
import GoogleSignIn

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

@main
struct iOSApp: App {
    init() {
        // TODO(APPS-13): FirebaseApp.configure() requires GoogleService-Info.plist to be
        // present in this target. No iOS app is registered in the Firebase console yet, so
        // this file does not exist in the repo (see .plans/APPS-13-plan.md, "Assumptions").
        // Register the iOS bundle id (pt.socialfood) in the Firebase console, download
        // GoogleService-Info.plist, and add it to the iosApp Xcode target before shipping —
        // without it, this call will crash at launch.
        FirebaseApp.configure()
        KoinKt.doInitKoin()
        GoogleSignInBridge.shared.delegate = GoogleSignInDelegateImpl()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}
