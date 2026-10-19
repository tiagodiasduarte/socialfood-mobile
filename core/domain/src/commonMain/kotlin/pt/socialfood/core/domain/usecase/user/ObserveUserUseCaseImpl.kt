package pt.socialfood.core.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.domain.repository.UsersRepository
import pt.socialfood.core.model.User

internal class ObserveUserUseCaseImpl(private val repository: UsersRepository) : ObserveUserUseCase {
    override operator fun invoke(): Flow<User?> = repository.currentUser
}
