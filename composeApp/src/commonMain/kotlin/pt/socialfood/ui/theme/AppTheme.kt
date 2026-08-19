package pt.socialfood.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val useDarkTheme = resolveUseDarkTheme()

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkLightColorTheme else LightColorTheme,
        typography = AppTypography,
        content = content,
    )
}

@Composable
internal expect fun resolveUseDarkTheme(): Boolean
