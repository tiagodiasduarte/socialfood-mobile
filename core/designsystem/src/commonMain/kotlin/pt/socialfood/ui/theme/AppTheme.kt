package pt.socialfood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The app's Material theme. The caller decides [darkTheme] (the app resolves the user's saved
 * theme mode), so the design system stays independent of settings and DI.
 */
@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkLightColorTheme else LightColorTheme,
        typography = AppTypography,
        content = content,
    )
}
