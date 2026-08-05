package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateGuideUseCaseImplTest {
    @Test
    fun `given no current user when invoked then returns Failure without calling the repository`() = runTest {
        // Given
        val guidesRepository = FakeGuidesRepository()
        val useCase = UpdateGuideUseCaseImpl(guidesRepository, FakeUsersRepository(currentUser = null))

        // When
        val result = useCase(
            id = Random.nextString(),
            title = Random.nextString(),
            description = Random.nextString(),
            restaurantIds = listOf(Random.nextString()),
            visibility = GuideVisibility.PUBLIC,
        )

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(0, guidesRepository.updateInvokeCount)
    }

    @Test
    fun `given a current user when invoked then forwards all fields to the repository`() = runTest {
        // Given
        val user = Random.nextUser()
        val guide = Random.nextGuide()
        val id = Random.nextString()
        val title = Random.nextString()
        val description = Random.nextString()
        val restaurantIds = listOf(Random.nextString())
        val guidesRepository = FakeGuidesRepository(updateResult = Result.Success(guide))
        val useCase = UpdateGuideUseCaseImpl(guidesRepository, FakeUsersRepository(currentUser = user))

        // When
        val result = useCase(
            id = id,
            title = title,
            description = description,
            restaurantIds = restaurantIds,
            visibility = GuideVisibility.PRIVATE,
        )

        // Then
        assertEquals(Result.Success(guide), result)
        assertEquals(id, guidesRepository.lastUpdateId)
        assertEquals(title, guidesRepository.lastUpdateName)
        assertEquals(user.id, guidesRepository.lastUpdateUserId)
        assertEquals(description, guidesRepository.lastUpdateDescription)
        assertEquals(restaurantIds, guidesRepository.lastUpdateRestaurantIds)
        assertEquals(GuideVisibility.PRIVATE, guidesRepository.lastUpdateVisibility)
    }

    @Test
    fun `given the repository fails when invoked then returns Failure`() = runTest {
        // Given
        val guidesRepository = FakeGuidesRepository(
            updateResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val usersRepository = FakeUsersRepository(currentUser = Random.nextUser())
        val useCase = UpdateGuideUseCaseImpl(guidesRepository, usersRepository)

        // When
        val result = useCase(
            id = Random.nextString(),
            title = Random.nextString(),
            description = Random.nextString(),
            restaurantIds = emptyList(),
            visibility = GuideVisibility.PUBLIC,
        )

        // Then
        assertIs<Result.Failure>(result)
    }
}
