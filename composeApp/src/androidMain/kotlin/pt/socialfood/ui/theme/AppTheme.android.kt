package pt.socialfood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository

@Composable
internal actual fun resolveUseDarkTheme(): Boolean {
    if (LocalInspectionMode.current) {
        return isSystemInDarkTheme()
    }

    val settingsRepository: SettingsRepository = remember { getKoin().get() }
    val themeMode by settingsRepository.observeThemeMode().collectAsStateWithLifecycle(initialValue = ThemeMode.LIGHT)

    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
}
