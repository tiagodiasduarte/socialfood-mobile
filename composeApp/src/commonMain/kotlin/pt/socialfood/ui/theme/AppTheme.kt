package pt.socialfood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.data.ThemeManager
import pt.socialfood.domain.model.ThemeMode

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val themeManager: ThemeManager = remember { getKoin().get() }
    val themeMode by themeManager.themeMode.collectAsStateWithLifecycle()

    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkLightColorTheme else LightColorTheme,
        typography = AppTypography,
        content = content,
    )
}
