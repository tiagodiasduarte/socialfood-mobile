package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.repository.AuthorsRepository

class GetUserAuthorProfileUseCaseImpl(private val repository: AuthorsRepository) : GetUserAuthorProfileUseCase {
    override suspend fun invoke(): Result<AuthorDetail> = repository.findUserAuthorProfile()
}
