package pt.socialfood.domain.usecase.theme

import pt.socialfood.domain.model.ThemeMode

interface SetThemeModeUseCase {
    suspend operator fun invoke(mode: ThemeMode)
}
