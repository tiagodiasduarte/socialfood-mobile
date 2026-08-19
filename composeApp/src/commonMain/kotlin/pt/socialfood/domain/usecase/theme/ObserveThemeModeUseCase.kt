package pt.socialfood.domain.usecase.theme

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.ThemeMode

interface ObserveThemeModeUseCase {
    operator fun invoke(): Flow<ThemeMode>
}
