package pt.socialfood.domain.use_case.user

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class ObserveUserUseCaseImpl(private val repository: UsersRepository) : ObserveUserUseCase {
    override operator fun invoke(): Flow<User?> = repository.currentUser
}
