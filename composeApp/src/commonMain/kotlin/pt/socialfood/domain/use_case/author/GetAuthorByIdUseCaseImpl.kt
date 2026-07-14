package pt.socialfood.domain.use_case.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.repository.AuthorsRepository

class GetAuthorByIdUseCaseImpl(
    private val repository: AuthorsRepository,
) : GetAuthorByIdUseCase {
    override suspend fun invoke(id: String): Result<AuthorDetail> =
        repository.findAuthorById(id)
}
