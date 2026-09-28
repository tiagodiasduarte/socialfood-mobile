package pt.socialfood.domain.usecase.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.usecase.favourite.restaurant.MarkRestaurantFavouriteUseCaseImpl
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MarkRestaurantFavouriteUseCaseImplTest {

    private val fakeRestaurant = Restaurant(
        id = "restaurant-id",
        name = "Restaurant Name",
        description = "Restaurant Description",
        city = "Lisbon",
        country = "Portugal",
        countryCode = "PT",
        postalCode = "1000-000",
        imagesUrl = emptyList(),
        address = "Rua Augusta 1",
        rating = 4.5,
        userRatingCount = 100,
        websiteUrl = null,
        phoneNumber = "+351910000000",
        location = Location(latitude = 38.7223, longitude = -9.1393),
    )

    @Test
    fun `given repository succeeds when invoked then delegates restaurant and returns Success`() = runTest {
        // Given
        val repository = FakeFavouriteRestaurantsRepository(markResult = Result.Success(Unit))
        val useCase = MarkRestaurantFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(fakeRestaurant)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(fakeRestaurant, repository.lastMarkedRestaurant)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeFavouriteRestaurantsRepository(markResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = MarkRestaurantFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(fakeRestaurant)

        // Then
        assertIs<Result.Failure>(result)
    }
}
