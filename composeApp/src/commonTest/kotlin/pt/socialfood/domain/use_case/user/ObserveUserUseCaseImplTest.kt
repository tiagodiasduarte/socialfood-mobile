package pt.socialfood.domain.use_case.user

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObserveUserUseCaseImplTest {
    @Test
    fun `given the repository emits a user when observed then forwards it`() = runTest {
        // Given
        val repository = FakeUsersRepository(currentUser = null)
        val useCase = ObserveUserUseCaseImpl(repository)

        // When / Then
        useCase().test {
            assertNull(awaitItem())

            val user = Random.nextUser()
            repository.emitCurrentUser(user)

            assertEquals(user, awaitItem())
        }
    }
}
