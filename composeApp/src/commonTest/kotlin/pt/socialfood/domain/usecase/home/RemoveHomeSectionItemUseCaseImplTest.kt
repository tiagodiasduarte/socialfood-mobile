package pt.socialfood.domain.usecase.home

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RemoveHomeSectionItemUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards sectionId and itemId`() = runTest {
        // Given
        val sectionId = Random.nextString()
        val itemId = Random.nextString()
        val repository = FakeHomeRepository(removeItemResult = Result.Success(true))
        val useCase = RemoveHomeSectionItemUseCaseImpl(repository)

        // When
        val result = useCase(sectionId, itemId)

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals(sectionId, repository.lastRemoveItemSectionId)
        assertEquals(itemId, repository.lastRemoveItemItemId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeHomeRepository(
            removeItemResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = RemoveHomeSectionItemUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString(), Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
