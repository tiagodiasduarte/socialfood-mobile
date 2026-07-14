package pt.socialfood.domain.use_case.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface AddRestaurantGuideUseCase {
    suspend operator fun invoke(
        guideId: String,
        placeId: String?,
    ): Result<Guide>
}