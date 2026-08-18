package pt.socialfood.domain.usecase.theme

import kotlinx.coroutines.flow.StateFlow
import pt.socialfood.domain.model.ThemeMode

interface ObserveThemeModeUseCase {
    operator fun invoke(): StateFlow<ThemeMode>
}
