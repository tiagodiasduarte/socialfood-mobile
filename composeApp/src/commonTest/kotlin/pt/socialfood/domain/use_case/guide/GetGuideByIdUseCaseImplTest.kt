package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetGuideByIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the guide and forwards the id`() = runTest {
        // Given
        val guide = Random.nextGuide()
        val repository = FakeGuidesRepository(findByIdResult = Result.Success(guide))
        val useCase = GetGuideByIdUseCaseImpl(repository)

        // When
        val result = useCase(guide.id)

        // Then
        assertEquals(Result.Success(guide), result)
        assertEquals(guide.id, repository.lastFindByIdId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository =
            FakeGuidesRepository(findByIdResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = GetGuideByIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
