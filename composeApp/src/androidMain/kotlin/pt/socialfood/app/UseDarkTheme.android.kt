package pt.socialfood.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.core.domain.repository.SettingsRepository
import pt.socialfood.core.model.ThemeMode

/** Android follows the Light/Dark/System choice saved in the in-app settings. */
@Composable
internal actual fun rememberUseDarkTheme(): Boolean {
    val settingsRepository: SettingsRepository = remember { getKoin().get() }
    val themeMode by settingsRepository.observeThemeMode().collectAsStateWithLifecycle(initialValue = ThemeMode.LIGHT)

    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
}
