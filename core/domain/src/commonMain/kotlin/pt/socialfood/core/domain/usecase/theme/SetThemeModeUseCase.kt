package pt.socialfood.core.domain.usecase.theme

import pt.socialfood.core.model.ThemeMode

interface SetThemeModeUseCase {
    suspend operator fun invoke(mode: ThemeMode)
}
