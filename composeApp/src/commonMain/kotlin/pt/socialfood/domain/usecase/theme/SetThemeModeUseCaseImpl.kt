package pt.socialfood.domain.usecase.theme

import pt.socialfood.data.ThemeManager
import pt.socialfood.domain.model.ThemeMode

class SetThemeModeUseCaseImpl(private val themeManager: ThemeManager) : SetThemeModeUseCase {
    override suspend operator fun invoke(mode: ThemeMode) {
        themeManager.setThemeMode(mode)
    }
}
