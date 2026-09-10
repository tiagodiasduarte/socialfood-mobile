package pt.socialfood.domain.usecase.author

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail

interface GetUserAuthorProfileUseCase {
    suspend operator fun invoke(): Result<AuthorDetail>
}
