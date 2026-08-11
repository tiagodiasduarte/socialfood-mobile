package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedAuthors

interface GetAuthorsUseCase {
    suspend operator fun invoke(page: Int, limit: Int): Result<PagedAuthors>
}
