package pt.socialfood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import pt.socialfood.domain.model.ThemeMode

/**
 * iOS always follows the system appearance -- there's no in-app Light/Dark override, since
 * iOS users already have that control in Settings > Display & Brightness.
 */
@Composable
internal actual fun resolveUseDarkTheme(themeMode: ThemeMode): Boolean = isSystemInDarkTheme()
