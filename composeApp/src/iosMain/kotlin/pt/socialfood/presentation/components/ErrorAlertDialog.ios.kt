package pt.socialfood.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ErrorAlertDialog(title: String, message: String, confirmButtonText: String, onDismiss: () -> Unit) {
    val viewController = LocalUIViewController.current
    LaunchedEffect(Unit) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = title,
            message = message,
            preferredStyle = UIAlertControllerStyleAlert,
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = confirmButtonText,
                style = UIAlertActionStyleDefault,
                handler = { onDismiss() },
            ),
        )
        viewController.presentViewController(alert, animated = true, completion = null)
    }
}
