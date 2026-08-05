package pt.socialfood.domain.use_case.home

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextHomeSection
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CreateHomeSectionUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards title, type and position`() = runTest {
        // Given
        val section = Random.nextHomeSection()
        val title = Random.nextString()
        val type: HomeSectionType = Random.nextEnum()
        val position = Random.nextInt(0, 10)
        val repository = FakeHomeRepository(createResult = Result.Success(section))
        val useCase = CreateHomeSectionUseCaseImpl(repository)

        // When
        val result = useCase(title, type, position)

        // Then
        assertEquals(Result.Success(section), result)
        assertEquals(title, repository.lastCreateTitle)
        assertEquals(type, repository.lastCreateType)
        assertEquals(position, repository.lastCreatePosition)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeHomeRepository(
            createResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = CreateHomeSectionUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString(), Random.nextEnum(), Random.nextInt(0, 10))

        // Then
        assertIs<Result.Failure>(result)
    }
}
