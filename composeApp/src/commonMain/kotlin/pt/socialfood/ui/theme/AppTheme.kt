package pt.socialfood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val useDarkTheme = isSystemInDarkTheme()
    val colorScheme = dynamicColorScheme(useDarkTheme)
        ?: if (useDarkTheme) DarkLightColorTheme else LightColorTheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
