package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class GetUsersUseCaseImpl(
    private val repository: UsersRepository,
) : GetUsersUseCase {
    override suspend operator fun invoke(): Result<List<User>> = repository.getUsers()
}
