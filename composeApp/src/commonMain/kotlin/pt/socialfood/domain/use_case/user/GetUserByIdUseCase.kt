package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User

interface GetUserByIdUseCase {
    suspend operator fun invoke(id: String): Result<User>
}
