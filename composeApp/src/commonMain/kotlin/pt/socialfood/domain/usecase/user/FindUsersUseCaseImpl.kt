package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.repository.UsersRepository

class FindUsersUseCaseImpl(
    private val usersRepository: UsersRepository,
) : FindUsersUseCase {
    override suspend fun invoke(page: Int, limit: Int, query: String?): Result<PagedUsers> =
        usersRepository.findUsers(page = page, limit = limit, query = query)
}
