package pt.socialfood.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.core.domain.repository.SettingsRepository
import pt.socialfood.core.domain.usecase.theme.SetThemeModeUseCase
import pt.socialfood.core.model.ThemeMode

class ThemeViewModel(settingsRepository: SettingsRepository, private val setThemeMode: SetThemeModeUseCase) :
    ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.observeThemeMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.LIGHT)

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeMode(mode)
        }
    }
}
