package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail

interface GetAuthorByIdUseCase {
    suspend operator fun invoke(id: String): Result<AuthorDetail>
}
