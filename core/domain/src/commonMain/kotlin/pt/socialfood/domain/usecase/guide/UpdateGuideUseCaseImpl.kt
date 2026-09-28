package pt.socialfood.domain.usecase.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.UsersRepository

class UpdateGuideUseCaseImpl(
    private val guidesRepository: GuidesRepository,
    private val userRepository: UsersRepository,
) : UpdateGuideUseCase {
    override suspend operator fun invoke(
        id: String,
        title: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> {
        val userId = userRepository.currentUser.first()?.id
            ?: return Result.Failure(DataError.Network(IllegalStateException("No current user")))

        return guidesRepository.update(
            id = id,
            name = title,
            userId = userId,
            description = description,
            restaurantIds = restaurantIds,
            visibility = visibility,
        )
    }
}
