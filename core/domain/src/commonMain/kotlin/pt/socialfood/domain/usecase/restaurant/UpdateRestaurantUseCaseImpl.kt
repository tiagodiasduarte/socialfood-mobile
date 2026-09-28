package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository

class UpdateRestaurantUseCaseImpl(private val repository: RestaurantsRepository) : UpdateRestaurantUseCase {
    override suspend operator fun invoke(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): Result<Restaurant> = repository.update(
        id = id,
        name = name,
        description = description,
        city = city,
        country = country,
        address = address,
        phoneNumber = phoneNumber,
        websiteUrl = websiteUrl,
    )
}
