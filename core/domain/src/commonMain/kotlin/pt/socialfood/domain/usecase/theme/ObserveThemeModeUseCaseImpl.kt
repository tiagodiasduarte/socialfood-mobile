package pt.socialfood.domain.usecase.theme

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository

class ObserveThemeModeUseCaseImpl(private val settingsRepository: SettingsRepository) : ObserveThemeModeUseCase {
    override operator fun invoke(): Flow<ThemeMode> = settingsRepository.observeThemeMode()
}
