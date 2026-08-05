package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CreateGuideUseCaseImplTest {
    @Test
    fun `given no current user when invoked then returns Failure without calling the repository`() = runTest {
        // Given
        val guidesRepository = FakeGuidesRepository()
        val useCase = CreateGuideUseCaseImpl(guidesRepository, FakeUsersRepository(currentUser = null))

        // When
        val result = useCase(title = "Guide Title", description = "Description", visibility = GuideVisibility.PUBLIC)

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(0, guidesRepository.createInvokeCount)
    }

    @Test
    fun `given a current user when invoked then forwards title, description and userId to the repository`() = runTest {
        // Given
        val user = Random.nextUser()
        val guide = Random.nextGuide()
        val guidesRepository = FakeGuidesRepository(createResult = Result.Success(guide))
        val useCase = CreateGuideUseCaseImpl(guidesRepository, FakeUsersRepository(currentUser = user))

        // When
        val result = useCase(title = "Guide Title", description = "Description", visibility = GuideVisibility.PRIVATE)

        // Then
        assertEquals(Result.Success(guide), result)
        assertEquals("Guide Title", guidesRepository.lastCreateName)
        assertEquals("Description", guidesRepository.lastCreateDescription)
        assertEquals(user.id, guidesRepository.lastCreateUserId)
    }

    @Test
    fun `given the repository fails when invoked then returns Failure`() = runTest {
        // Given
        val guidesRepository =
            FakeGuidesRepository(createResult = Result.Failure(DataError.Network(Exception("test error"))))
        val usersRepository = FakeUsersRepository(currentUser = Random.nextUser())
        val useCase = CreateGuideUseCaseImpl(guidesRepository, usersRepository)

        // When
        val result = useCase(title = "Guide Title", description = "Description", visibility = GuideVisibility.PUBLIC)

        // Then
        assertIs<Result.Failure>(result)
    }
}
