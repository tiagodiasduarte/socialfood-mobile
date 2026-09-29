package pt.socialfood.presentation.guide

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.presentation.guide.all.AllGuidesViewModel
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AllGuidesViewModelTest {
    private fun createViewModel(
        guidesRepository: GuidesRepository = FakeGuidesRepository(),
        observeUser: ObserveUserUseCase = FakeObserveUserUseCase(Random.nextUser()),
        favouritesGuidesRepository: FavouritesGuidesRepository = FakeFavouritesGuidesRepository(),
    ) = AllGuidesViewModel(
        guidesRepository,
        favouritesGuidesRepository,
        observeUser,
    )

    @Test
    fun `given the viewmodel is created when guides is collected then the guides paging flow is requested`() =
        runTestWithMainDispatcher {
            // Given
            val guidesRepository = FakeGuidesRepository()
            val vm = createViewModel(guidesRepository = guidesRepository)

            // When
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, guidesRepository.findGuidesPagingInvokeCount)
            job.cancel()
        }

    @Test
    fun `given the current user is observed then user reflects the emitted value`() = runTestWithMainDispatcher {
        // Given
        val user = Random.nextUser()
        val observeUser = FakeObserveUserUseCase(initial = user)

        // When / Then
        val vm = createViewModel(observeUser = observeUser)
        vm.user.test {
            awaitItem()
            assertEquals(user, awaitItem())
        }
    }

    @Test
    fun `given favourite ids are observed then favouriteGuideIds reflects them`() = runTestWithMainDispatcher {
        // Given
        val guideId = Random.nextString()
        val favouritesGuidesRepository = FakeFavouritesGuidesRepository(favouriteGuideIds = setOf(guideId))

        // When
        val vm = createViewModel(favouritesGuidesRepository = favouritesGuidesRepository)
        advanceUntilIdle()

        // Then
        assertEquals(setOf(guideId), vm.favouriteGuideIds.value)
    }

    @Test
    fun `given a guide is not favourited when onToggleGuideFavourite is called then it is marked`() =
        runTestWithMainDispatcher {
            // Given
            val favouritesGuidesRepository = FakeFavouritesGuidesRepository()
            val vm =
                createViewModel(
                    favouritesGuidesRepository = favouritesGuidesRepository,
                )
            val target = Random.nextGuide()

            // When
            vm.onToggleGuideFavourite(target)
            advanceUntilIdle()

            // Then
            assertEquals(target, favouritesGuidesRepository.lastMarkedGuide)
            assertEquals(0, favouritesGuidesRepository.unmarkInvokeCount)
        }

    @Test
    fun `given a favourited guide when onToggleGuideFavourite is called then it is unmarked`() =
        runTestWithMainDispatcher {
            // Given
            val guideId = Random.nextString()
            val favouritesGuidesRepository = FakeFavouritesGuidesRepository(favouriteGuideIds = setOf(guideId))
            val vm =
                createViewModel(
                    favouritesGuidesRepository = favouritesGuidesRepository,
                )
            val target = Random.nextGuide(id = guideId)
            advanceUntilIdle()

            // When
            vm.onToggleGuideFavourite(target)
            advanceUntilIdle()

            // Then
            assertEquals(guideId, favouritesGuidesRepository.lastUnmarkedGuideId)
            assertEquals(0, favouritesGuidesRepository.markInvokeCount)
        }
}
