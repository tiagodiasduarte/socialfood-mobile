package pt.socialfood.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository

class ThemeManager(private val settingsRepository: SettingsRepository) {

    private val _themeMode = MutableStateFlow(runBlocking { settingsRepository.getThemeMode() })
    val themeMode: StateFlow<ThemeMode> = _themeMode

    suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        settingsRepository.saveThemeMode(mode)
    }
}
