package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ApiError
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.UsersRepository

class AddRestaurantGuideUseCaseImpl(
    private val guidesRepository: GuidesRepository,
    private val userRepository: UsersRepository,
) : AddRestaurantGuideUseCase {
    override suspend operator fun invoke(
        guideId: String,
        placeId: String?,
    ): Result<Guide> {
        val userId =
            userRepository.currentUser.first()?.id
                ?: return Result.Failure(ApiError.Network(IllegalStateException("No current user")))

        return guidesRepository.addRestaurantGuide(
            guideId = guideId,
            userId = userId,
            placeId = placeId,
        )
    }
}
