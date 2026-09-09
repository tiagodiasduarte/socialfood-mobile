import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseCrashlytics
import GoogleSignIn

@main
struct iOSApp: App {
    init() {
        #if DEBUG
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
        #endif
        FirebaseApp.configure()
        GoogleSignInBridge.shared.delegate = GoogleSignInDelegateImpl()
        ImagePickerBridge.shared.delegate = ImagePickerDelegateImpl()
        JoinSharedGuideCodeBridge.shared.delegate = JoinSharedGuideCodeDelegateImpl()
        let alertDialogDelegate = AlertDialogDelegateImpl()
        DeleteGuideConfirmationBridge.shared.delegate = alertDialogDelegate
        GuideValidationErrorDialogBridge.shared.delegate = alertDialogDelegate
        KoinKt.doInitKoin()
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
