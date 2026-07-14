package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User

interface GetUserMeUseCase {
    suspend operator fun invoke(): Result<User>
}
