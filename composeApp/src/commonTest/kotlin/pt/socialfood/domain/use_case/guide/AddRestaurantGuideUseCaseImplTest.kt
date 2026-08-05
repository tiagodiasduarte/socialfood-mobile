package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddRestaurantGuideUseCaseImplTest {
    @Test
    fun `given no current user when invoked then returns Failure without calling the repository`() = runTest {
        // Given
        val guidesRepository = FakeGuidesRepository()
        val useCase = AddRestaurantGuideUseCaseImpl(guidesRepository, FakeUsersRepository(currentUser = null))

        // When
        val result = useCase(guideId = Random.nextString(), placeId = Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(0, guidesRepository.addRestaurantGuideInvokeCount)
    }

    @Test
    fun `given a current user when invoked then forwards guideId userId and placeId to the repository`() = runTest {
        // Given
        val user = Random.nextUser()
        val guide = Random.nextGuide()
        val guideId = Random.nextString()
        val placeId = Random.nextString()
        val guidesRepository = FakeGuidesRepository(addRestaurantGuideResult = Result.Success(guide))
        val useCase = AddRestaurantGuideUseCaseImpl(guidesRepository, FakeUsersRepository(currentUser = user))

        // When
        val result = useCase(guideId = guideId, placeId = placeId)

        // Then
        assertEquals(Result.Success(guide), result)
        assertEquals(guideId, guidesRepository.lastAddRestaurantGuideId)
        assertEquals(user.id, guidesRepository.lastAddRestaurantUserId)
        assertEquals(placeId, guidesRepository.lastAddRestaurantPlaceId)
    }

    @Test
    fun `given the repository fails when invoked then returns Failure`() = runTest {
        // Given
        val guidesRepository = FakeGuidesRepository(
            addRestaurantGuideResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val usersRepository = FakeUsersRepository(currentUser = Random.nextUser())
        val useCase = AddRestaurantGuideUseCaseImpl(guidesRepository, usersRepository)

        // When
        val result = useCase(guideId = Random.nextString(), placeId = null)

        // Then
        assertIs<Result.Failure>(result)
    }
}
