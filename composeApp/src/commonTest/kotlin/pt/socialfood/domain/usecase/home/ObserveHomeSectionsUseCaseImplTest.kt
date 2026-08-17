package pt.socialfood.domain.usecase.home

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.random.nextHomeSection
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveHomeSectionsUseCaseImplTest {
    @Test
    fun `given the repository emits sections when observed then forwards them`() = runTest {
        // Given
        val initialSections = listOf(Random.nextHomeSection())
        val repository = FakeHomeRepository(homeSections = initialSections)
        val useCase = ObserveHomeSectionsUseCaseImpl(repository)

        // When / Then
        useCase().test {
            assertEquals(initialSections, awaitItem())

            val updatedSections = listOf(Random.nextHomeSection(), Random.nextHomeSection())
            repository.emitHomeSections(updatedSections)

            assertEquals(updatedSections, awaitItem())
        }
    }
}
