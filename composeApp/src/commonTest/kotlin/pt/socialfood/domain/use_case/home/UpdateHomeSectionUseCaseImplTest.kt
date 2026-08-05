package pt.socialfood.domain.use_case.home

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.random.nextHomeSection
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateHomeSectionUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards all fields`() = runTest {
        // Given
        val section = Random.nextHomeSection()
        val id = Random.nextString()
        val title = Random.nextString()
        val position = Random.nextInt(0, 10)
        val isActive = Random.nextBoolean()
        val restaurantIds = listOf(Random.nextString())
        val guideIds = listOf(Random.nextString())
        val repository = FakeHomeRepository(updateResult = Result.Success(section))
        val useCase = UpdateHomeSectionUseCaseImpl(repository)

        // When
        val result = useCase(
            id = id,
            title = title,
            position = position,
            isActive = isActive,
            restaurantIds = restaurantIds,
            guideIds = guideIds,
        )

        // Then
        assertEquals(Result.Success(section), result)
        assertEquals(id, repository.lastUpdateId)
        assertEquals(title, repository.lastUpdateTitle)
        assertEquals(position, repository.lastUpdatePosition)
        assertEquals(isActive, repository.lastUpdateIsActive)
        assertEquals(restaurantIds, repository.lastUpdateRestaurantIds)
        assertEquals(guideIds, repository.lastUpdateGuideIds)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeHomeRepository(
            updateResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = UpdateHomeSectionUseCaseImpl(repository)

        // When
        val result = useCase(
            id = Random.nextString(),
            title = Random.nextString(),
            position = Random.nextInt(0, 10),
            isActive = Random.nextBoolean(),
        )

        // Then
        assertIs<Result.Failure>(result)
    }
}
