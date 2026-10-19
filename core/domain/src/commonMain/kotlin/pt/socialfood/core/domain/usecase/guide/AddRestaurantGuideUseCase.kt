package pt.socialfood.core.domain.usecase.guide

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Guide

interface AddRestaurantGuideUseCase {
    suspend operator fun invoke(guideId: String, placeId: String?): Result<Guide>
}
