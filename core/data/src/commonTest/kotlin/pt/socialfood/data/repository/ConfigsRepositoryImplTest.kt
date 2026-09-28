package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Configs
import pt.socialfood.fakes.FakeConfigsApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConfigsRepositoryImplTest {

    @Test
    fun `given configs exist when getConfigs is called then returns Success with Configs`() = runTest {
        // Given
        val repo = ConfigsRepositoryImpl(FakeConfigsApi())

        // When
        val result = repo.getConfigs()

        // Then
        assertIs<Result.Success<Configs>>(result)
        assertEquals("1.0.0", result.data.version)
    }

    @Test
    fun `given api throws when getConfigs is called then returns Error Unknown`() = runTest {
        // Given
        val repo = ConfigsRepositoryImpl(FakeConfigsApi(shouldThrow = true))

        // When
        val result = repo.getConfigs()

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }
}
