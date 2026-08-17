package pt.socialfood.presentation.imagepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Swift side must implement this interface and assign it to ImagePickerBridge.delegate.
// Example Swift implementation using PHPickerViewController:
//
//   class ImagePickerDelegateImpl: NSObject, ImagePickerDelegate,
//       PHPickerViewControllerDelegate {
//
//     var rootViewController: UIViewController
//     init(rootViewController: UIViewController) { self.rootViewController = rootViewController }
//
//     func pickImage(onResult: @escaping (KotlinByteArray, String) -> Void) {
//       var config = PHPickerConfiguration()
//       config.filter = .images
//       config.selectionLimit = 1
//       let picker = PHPickerViewController(configuration: config)
//       picker.delegate = self
//       rootViewController.present(picker, animated: true)
//       self.pendingCallback = onResult
//     }
//
//     private var pendingCallback: ((KotlinByteArray, String) -> Void)?
//
//     func picker(_ picker: PHPickerViewController,
//                 didFinishPicking results: [PHPickerResult]) {
//       picker.dismiss(animated: true)
//       guard let provider = results.first?.itemProvider,
//             provider.hasItemConformingToTypeIdentifier("public.image") else { return }
//       provider.loadDataRepresentation(forTypeIdentifier: "public.image") { data, _ in
//         guard let data = data else { return }
//         let kotlinBytes = KotlinByteArray(size: Int32(data.count))
//         for (i, byte) in data.enumerated() { kotlinBytes.set(index: Int32(i), value: Int8(bitPattern: byte)) }
//         DispatchQueue.main.async { self.pendingCallback?(kotlinBytes, "image/jpeg") }
//       }
//     }
//   }
//
//   // In your iOS app entry point:
//   ImagePickerBridge.shared.delegate = ImagePickerDelegateImpl(rootViewController: window.rootViewController!)

interface ImagePickerDelegate {
    fun pickImage(onResult: (ByteArray, String) -> Unit)
}

object ImagePickerBridge {
    var delegate: ImagePickerDelegate? = null
}

@Composable
actual fun rememberImagePickerLauncher(onResult: (bytes: ByteArray, mimeType: String) -> Unit): () -> Unit =
    remember(onResult) {
        { ImagePickerBridge.delegate?.pickImage(onResult) }
    }
