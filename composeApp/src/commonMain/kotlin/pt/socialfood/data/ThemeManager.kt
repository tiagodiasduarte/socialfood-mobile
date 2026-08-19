package pt.socialfood.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository

class ThemeManager(private val settingsRepository: SettingsRepository) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    init {
        scope.launch {
            _themeMode.value = settingsRepository.getThemeMode()
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        settingsRepository.saveThemeMode(mode)
    }
}
