import SwiftUI
import ComposeApp
import GoogleSignIn
import PhotosUI

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

class ImagePickerDelegateImpl: NSObject, ImagePickerDelegate, PHPickerViewControllerDelegate {
    private var pendingCallback: ((KotlinByteArray, String) -> Void)?

    func pickImage(onResult: @escaping (KotlinByteArray, String) -> Void) {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = scene.windows.first?.rootViewController else {
            return
        }
        var config = PHPickerConfiguration()
        config.filter = .images
        config.selectionLimit = 1
        let picker = PHPickerViewController(configuration: config)
        picker.delegate = self
        pendingCallback = onResult
        rootVC.present(picker, animated: true)
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)
        guard let provider = results.first?.itemProvider,
              provider.hasItemConformingToTypeIdentifier("public.image") else { return }
        provider.loadDataRepresentation(forTypeIdentifier: "public.image") { data, _ in
            guard let data = data else { return }
            let kotlinBytes = KotlinByteArray(size: Int32(data.count))
            for (i, byte) in data.enumerated() { kotlinBytes.set(index: Int32(i), value: Int8(bitPattern: byte)) }
            DispatchQueue.main.async { self.pendingCallback?(kotlinBytes, "image/jpeg") }
        }
    }
}

@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
        GoogleSignInBridge.shared.delegate = GoogleSignInDelegateImpl()
        ImagePickerBridge.shared.delegate = ImagePickerDelegateImpl()
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
