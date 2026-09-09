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
        DeleteGuideConfirmationBridge.shared.delegate = DeleteGuideConfirmationDelegateImpl()
        GuideValidationErrorDialogBridge.shared.delegate = GuideValidationErrorDialogDelegateImpl()
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
