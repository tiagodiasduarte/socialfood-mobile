package pt.socialfood.core.domain.usecase.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.common.Result
import pt.socialfood.core.common.error.DataError
import pt.socialfood.core.domain.repository.GuidesRepository
import pt.socialfood.core.domain.repository.UsersRepository
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

internal class UpdateGuideUseCaseImpl(
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
