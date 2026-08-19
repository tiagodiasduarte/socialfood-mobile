package pt.socialfood.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.resources.stringResource
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import pt.socialfood.domain.model.ThemeMode
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.theme_dark_label
import socialfood.composeapp.generated.resources.theme_light_label
import socialfood.composeapp.generated.resources.theme_sheet_cancel_button
import socialfood.composeapp.generated.resources.theme_sheet_title
import socialfood.composeapp.generated.resources.theme_system_label

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun ThemeBottomSheetContent(
    selectedThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val viewController = LocalUIViewController.current
    val title = stringResource(Res.string.theme_sheet_title)
    val lightLabel = stringResource(Res.string.theme_light_label)
    val darkLabel = stringResource(Res.string.theme_dark_label)
    val systemLabel = stringResource(Res.string.theme_system_label)
    val cancelLabel = stringResource(Res.string.theme_sheet_cancel_button)

    LaunchedEffect(Unit) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = title,
            message = null,
            preferredStyle = UIAlertControllerStyleActionSheet,
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = lightLabel,
                style = UIAlertActionStyleDefault,
                handler = { onThemeModeSelected(ThemeMode.LIGHT) },
            ),
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = darkLabel,
                style = UIAlertActionStyleDefault,
                handler = { onThemeModeSelected(ThemeMode.DARK) },
            ),
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = systemLabel,
                style = UIAlertActionStyleDefault,
                handler = { onThemeModeSelected(ThemeMode.SYSTEM) },
            ),
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = cancelLabel,
                style = UIAlertActionStyleCancel,
                handler = { onDismiss() },
            ),
        )
        viewController.presentViewController(alert, animated = true, completion = null)
    }
}
