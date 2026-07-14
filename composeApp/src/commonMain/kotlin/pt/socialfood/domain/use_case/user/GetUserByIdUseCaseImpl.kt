package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class GetUserByIdUseCaseImpl(
    private val repository: UsersRepository,
) : GetUserByIdUseCase {
    override suspend operator fun invoke(id: String): Result<User> = repository.findById(id)
}
