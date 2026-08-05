package pt.socialfood.domain.use_case.home

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextHomeSection
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddHomeSectionItemUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards sectionId, itemId, itemType and position`() = runTest {
        // Given
        val section = Random.nextHomeSection()
        val sectionId = Random.nextString()
        val itemId = Random.nextString()
        val itemType: HomeItemType = Random.nextEnum()
        val position = Random.nextInt(0, 10)
        val repository = FakeHomeRepository(addItemResult = Result.Success(section))
        val useCase = AddHomeSectionItemUseCaseImpl(repository)

        // When
        val result = useCase(sectionId = sectionId, itemId = itemId, itemType = itemType, position = position)

        // Then
        assertEquals(Result.Success(section), result)
        assertEquals(sectionId, repository.lastAddItemSectionId)
        assertEquals(itemId, repository.lastAddItemItemId)
        assertEquals(itemType, repository.lastAddItemItemType)
        assertEquals(position, repository.lastAddItemPosition)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeHomeRepository(
            addItemResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = AddHomeSectionItemUseCaseImpl(repository)

        // When
        val result = useCase(
            sectionId = Random.nextString(),
            itemId = Random.nextString(),
            itemType = Random.nextEnum(),
            position = Random.nextInt(0, 10),
        )

        // Then
        assertIs<Result.Failure>(result)
    }
}
