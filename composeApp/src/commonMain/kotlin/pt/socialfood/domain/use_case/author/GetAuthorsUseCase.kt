package pt.socialfood.domain.use_case.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedAuthors

interface GetAuthorsUseCase {
    suspend operator fun invoke(page: Int, limit: Int): Result<PagedAuthors>
}
