package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.domain.use_case.guide.validation.validateGuideInput

class CreateGuideUseCaseImpl(
    private val guidesRepository: GuidesRepository,
    private val userRepository: UsersRepository,
) : CreateGuideUseCase {
    override suspend operator fun invoke(
        title: String,
        description: String,
        visibility: GuideVisibility,
        restaurantIds: List<String>?,
    ): Result<Guide> {
        validateGuideInput(
            title = title,
            description = description,
            visibility = visibility,
            restaurantIds = restaurantIds,
            requireDescription = false,
        )?.let { return Result.Error(it) }

        val userId = userRepository.currentUser.first()?.id ?: return Result.Error(ErrorEntity.Unknown)

        return guidesRepository.create(
            name = title,
            description = description,
            userId = userId,
        )
    }
}
