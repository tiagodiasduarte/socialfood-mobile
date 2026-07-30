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

class CrashReportingDelegateImpl: CrashReportingDelegate {
    func triggerTestCrash() {
        fatalError("Test Crash - SocialFood Profile debug trigger")
    }
}

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        KoinKt.doInitKoin()
        GoogleSignInBridge.shared.delegate = GoogleSignInDelegateImpl()
        ImagePickerBridge.shared.delegate = ImagePickerDelegateImpl()
        CrashReportingBridge.shared.delegate = CrashReportingDelegateImpl()
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
