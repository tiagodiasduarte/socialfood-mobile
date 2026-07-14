package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.domain.use_case.guide.validation.validateGuideInput

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
        validateGuideInput(
            title = title,
            description = description,
            visibility = visibility,
            restaurantIds = restaurantIds,
        )?.let { return Result.Error(it) }

        val userId = userRepository.currentUser.first()?.id ?: return Result.Error(ErrorEntity.Unknown)

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
