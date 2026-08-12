package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedUsers

interface FindUsersUseCase {
    suspend operator fun invoke(page: Int, limit: Int, query: String? = null): Result<PagedUsers>
}
