package pt.socialfood.domain.usecase.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class JoinGuideUseCaseImplTest {
    @Test
    fun `given a guideId when invoked then forwards it and returns the repository's result`() = runTest {
        // Given
        val guideId = Random.nextString()
        val guide = Random.nextGuide()
        val result = Result.Success(guide)
        val repository = FakeGuidesRepository(joinGuideResult = result)
        val useCase = JoinGuideUseCaseImpl(repository)

        // When
        val actual = useCase(guideId)

        // Then
        assertSame(result, actual)
        assertEquals(guideId, repository.lastJoinGuideId)
    }
}
