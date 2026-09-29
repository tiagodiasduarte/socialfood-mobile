package pt.socialfood.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.domain.usecase.theme.SetThemeModeUseCase

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
