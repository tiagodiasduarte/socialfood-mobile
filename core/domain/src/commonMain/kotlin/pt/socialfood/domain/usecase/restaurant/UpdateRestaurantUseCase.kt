package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface UpdateRestaurantUseCase {
    suspend operator fun invoke(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): Result<Restaurant>
}
