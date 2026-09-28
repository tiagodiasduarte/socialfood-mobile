package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedAuthors

interface FindAuthorsUseCase {
    suspend operator fun invoke(page: Int, limit: Int, query: String? = null): Result<PagedAuthors>
}
