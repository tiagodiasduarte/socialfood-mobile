package pt.socialfood.domain.usecase.theme

import kotlinx.coroutines.flow.StateFlow
import pt.socialfood.data.ThemeManager
import pt.socialfood.domain.model.ThemeMode

class ObserveThemeModeUseCaseImpl(private val themeManager: ThemeManager) : ObserveThemeModeUseCase {
    override operator fun invoke(): StateFlow<ThemeMode> = themeManager.themeMode
}
