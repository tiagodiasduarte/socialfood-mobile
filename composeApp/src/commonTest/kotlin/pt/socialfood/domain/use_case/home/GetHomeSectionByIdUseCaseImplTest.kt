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

class GetHomeSectionByIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the section and forwards the id`() = runTest {
        // Given
        val section = Random.nextHomeSection()
        val repository = FakeHomeRepository(findByIdResult = Result.Success(section))
        val useCase = GetHomeSectionByIdUseCaseImpl(repository)

        // When
        val result = useCase(section.id)

        // Then
        assertEquals(Result.Success(section), result)
        assertEquals(section.id, repository.lastFindByIdId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeHomeRepository(
            findByIdResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetHomeSectionByIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
