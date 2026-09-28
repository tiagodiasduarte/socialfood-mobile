package pt.socialfood.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.User

interface ObserveUserUseCase {
    operator fun invoke(): Flow<User?>
}
