package pt.socialfood.domain.usecase.restaurant

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRestaurantsRepository
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateRestaurantUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards all fields`() = runTest {
        // Given
        val restaurant = Random.nextRestaurant()
        val id = Random.nextString()
        val name = Random.nextString()
        val description = Random.nextString()
        val country = Random.nextString()
        val city = Random.nextString()
        val address = Random.nextString()
        val phoneNumber = Random.nextString()
        val websiteUrl = Random.nextString()
        val repository = FakeRestaurantsRepository(updateResult = Result.Success(restaurant))
        val useCase = UpdateRestaurantUseCaseImpl(repository)

        // When
        val result = useCase(
            id = id,
            name = name,
            description = description,
            country = country,
            city = city,
            address = address,
            phoneNumber = phoneNumber,
            websiteUrl = websiteUrl,
        )

        // Then
        assertEquals(Result.Success(restaurant), result)
        assertEquals(id, repository.lastUpdateId)
        assertEquals(name, repository.lastUpdateName)
        assertEquals(description, repository.lastUpdateDescription)
        assertEquals(country, repository.lastUpdateCountry)
        assertEquals(city, repository.lastUpdateCity)
        assertEquals(address, repository.lastUpdateAddress)
        assertEquals(phoneNumber, repository.lastUpdatePhoneNumber)
        assertEquals(websiteUrl, repository.lastUpdateWebsiteUrl)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeRestaurantsRepository(
            updateResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = UpdateRestaurantUseCaseImpl(repository)

        // When
        val result = useCase(
            id = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextString(),
            country = Random.nextString(),
            city = Random.nextString(),
            address = Random.nextString(),
            phoneNumber = Random.nextString(),
            websiteUrl = Random.nextString(),
        )

        // Then
        assertIs<Result.Failure>(result)
    }
}
