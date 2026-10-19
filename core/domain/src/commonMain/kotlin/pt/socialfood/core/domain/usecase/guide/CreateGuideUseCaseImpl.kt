package pt.socialfood.core.domain.usecase.guide

import kotlinx.coroutines.flow.first
import pt.socialfood.core.common.Result
import pt.socialfood.core.common.error.DataError
import pt.socialfood.core.domain.repository.GuidesRepository
import pt.socialfood.core.domain.repository.UsersRepository
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

internal class CreateGuideUseCaseImpl(
    private val guidesRepository: GuidesRepository,
    private val userRepository: UsersRepository,
) : CreateGuideUseCase {
    override suspend operator fun invoke(
        title: String,
        description: String,
        visibility: GuideVisibility,
        restaurantIds: List<String>?,
    ): Result<Guide> {
        val userId = userRepository.currentUser.first()?.id
            ?: return Result.Failure(DataError.Network(IllegalStateException("No current user")))

        return guidesRepository.create(
            name = title,
            description = description,
            userId = userId,
        )
    }
}
