package pt.socialfood.domain.use_case.user

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.User

interface ObserveUserUseCase {
    operator fun invoke(): Flow<User?>
}
