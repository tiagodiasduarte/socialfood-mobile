package pt.socialfood.core.domain.usecase.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.common.Result
import pt.socialfood.core.common.error.DataError
import pt.socialfood.core.domain.repository.GuidesRepository
import pt.socialfood.core.domain.repository.UsersRepository
import pt.socialfood.core.model.Guide

internal class AddRestaurantGuideUseCaseImpl(
    private val guidesRepository: GuidesRepository,
    private val userRepository: UsersRepository,
) : AddRestaurantGuideUseCase {
    override suspend operator fun invoke(guideId: String, placeId: String?): Result<Guide> {
        val userId =
            userRepository.currentUser.first()?.id
                ?: return Result.Failure(DataError.Network(IllegalStateException("No current user")))

        return guidesRepository.addRestaurantGuide(
            guideId = guideId,
            userId = userId,
            placeId = placeId,
        )
    }
}
