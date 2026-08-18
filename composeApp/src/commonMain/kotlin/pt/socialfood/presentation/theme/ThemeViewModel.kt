package pt.socialfood.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.usecase.theme.ObserveThemeModeUseCase
import pt.socialfood.domain.usecase.theme.SetThemeModeUseCase

class ThemeViewModel(observeThemeMode: ObserveThemeModeUseCase, private val setThemeMode: SetThemeModeUseCase) :
    ViewModel() {

    val themeMode: StateFlow<ThemeMode> = observeThemeMode()

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeMode(mode)
        }
    }
}
