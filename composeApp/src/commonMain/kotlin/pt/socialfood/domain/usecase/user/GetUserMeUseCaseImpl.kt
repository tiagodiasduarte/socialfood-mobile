package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class GetUserMeUseCaseImpl(
    private val repository: UsersRepository,
) : GetUserMeUseCase {
    override suspend operator fun invoke(): Result<User> = repository.getUserMe()
}
