package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.repository.AuthorsRepository

class GetAuthorsUseCaseImpl(
    private val repository: AuthorsRepository,
) : GetAuthorsUseCase {
    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedAuthors> =
        repository.findAuthors(page = page, limit = limit)
}
