package pt.socialfood.core.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.model.User

interface ObserveUserUseCase {
    operator fun invoke(): Flow<User?>
}
