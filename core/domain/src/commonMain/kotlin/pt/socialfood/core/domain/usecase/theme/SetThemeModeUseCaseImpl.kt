package pt.socialfood.core.domain.usecase.theme

import pt.socialfood.core.domain.repository.SettingsRepository
import pt.socialfood.core.model.ThemeMode

internal class SetThemeModeUseCaseImpl(private val settingsRepository: SettingsRepository) : SetThemeModeUseCase {
    override suspend operator fun invoke(mode: ThemeMode) {
        settingsRepository.saveThemeMode(mode)
    }
}
