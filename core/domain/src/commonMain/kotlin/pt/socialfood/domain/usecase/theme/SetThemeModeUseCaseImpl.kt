package pt.socialfood.domain.usecase.theme

import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository

class SetThemeModeUseCaseImpl(private val settingsRepository: SettingsRepository) : SetThemeModeUseCase {
    override suspend operator fun invoke(mode: ThemeMode) {
        settingsRepository.saveThemeMode(mode)
    }
}
