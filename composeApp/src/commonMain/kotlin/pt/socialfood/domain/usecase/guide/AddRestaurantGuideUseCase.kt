package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface AddRestaurantGuideUseCase {
    suspend operator fun invoke(guideId: String, placeId: String?): Result<Guide>
}
