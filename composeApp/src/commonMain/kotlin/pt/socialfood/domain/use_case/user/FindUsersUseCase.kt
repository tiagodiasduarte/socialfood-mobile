package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedUsers

interface FindUsersUseCase {
    suspend operator fun invoke(page: Int, limit: Int, query: String? = null): Result<PagedUsers>
}
