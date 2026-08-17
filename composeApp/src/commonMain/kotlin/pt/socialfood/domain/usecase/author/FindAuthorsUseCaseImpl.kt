package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.repository.AuthorsRepository

class FindAuthorsUseCaseImpl(private val repository: AuthorsRepository) : FindAuthorsUseCase {
    override suspend fun invoke(page: Int, limit: Int, query: String?): Result<PagedAuthors> =
        repository.findAuthors(page = page, limit = limit, query = query)
}
