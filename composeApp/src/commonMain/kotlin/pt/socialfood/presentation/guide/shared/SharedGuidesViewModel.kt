package pt.socialfood.presentation.guide.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.guide.FindGuideBySharedCodeUseCase
import pt.socialfood.domain.usecase.guide.GetJoinedGuidesPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.presentation.error.toErrorCode

@OptIn(ExperimentalCoroutinesApi::class)
class SharedGuidesViewModel(
    getJoinedGuidesPaging: GetJoinedGuidesPagingUseCase,
    private val findGuideBySharedCode: FindGuideBySharedCodeUseCase,
    private val markGuideFavourite: MarkGuideFavouriteUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
    observeUser: ObserveUserUseCase,
    observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase,
) : ViewModel() {

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val guides: Flow<PagingData<Guide>> = observeUser()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { userId -> getJoinedGuidesPaging(userId) }
        .cachedIn(viewModelScope)

    val favouriteGuideIds: StateFlow<Set<String>> = observeFavouriteGuideIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    private val _joinGuideState = MutableStateFlow<JoinGuideUiState>(JoinGuideUiState.Idle)
    val joinGuideState: StateFlow<JoinGuideUiState> = _joinGuideState

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    fun onToggleGuideFavourite(guide: Guide) {
        viewModelScope.launch {
            if (guide.id in favouriteGuideIds.value) {
                unmarkGuideFavourite(guide.id)
            } else {
                markGuideFavourite(guide)
            }
        }
    }

    fun onJoinGuide(code: String) {
        viewModelScope.launch {
            _joinGuideState.value = JoinGuideUiState.Loading

            when (val result = findGuideBySharedCode(code)) {
                is Result.Success -> {
                    _joinGuideState.value = JoinGuideUiState.Idle
                    _events.emit(UiEvent.GuideJoined(result.data.id))
                }

                is Result.Failure -> _joinGuideState.value = JoinGuideUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun onDismissJoinGuideError() {
        _joinGuideState.value = JoinGuideUiState.Idle
    }

    sealed interface UiEvent {
        data class GuideJoined(val guideId: String) : UiEvent
    }
}
