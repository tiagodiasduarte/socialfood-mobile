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
import pt.socialfood.domain.usecase.guide.GetGuideBySharedCodeUseCase
import pt.socialfood.domain.usecase.guide.GetUserJoinedGuidesPagingUseCase
import pt.socialfood.domain.usecase.guide.JoinGuideUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.presentation.error.toErrorCode
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideCardUiState
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideDialogUiState

@OptIn(ExperimentalCoroutinesApi::class)
class SharedGuidesViewModel(
    getUserJoinedGuidesPaging: GetUserJoinedGuidesPagingUseCase,
    private val getGuideBySharedCode: GetGuideBySharedCodeUseCase,
    private val joinGuide: JoinGuideUseCase,
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
        .flatMapLatest { userId -> getUserJoinedGuidesPaging(userId) }
        .cachedIn(viewModelScope)

    val favouriteGuideIds: StateFlow<Set<String>> = observeFavouriteGuideIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    private val _joinGuideState = MutableStateFlow<JoinSharedGuideDialogUiState>(JoinSharedGuideDialogUiState.Idle)
    val joinGuideState: StateFlow<JoinSharedGuideDialogUiState> = _joinGuideState

    private val _guideToJoin = MutableStateFlow<JoinSharedGuideCardUiState?>(null)
    val guideToJoin: StateFlow<JoinSharedGuideCardUiState?> = _guideToJoin

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private var joinCode: String? = null

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
            _joinGuideState.value = JoinSharedGuideDialogUiState.Loading

            when (val result = getGuideBySharedCode(code)) {
                is Result.Success -> {
                    _joinGuideState.value = JoinSharedGuideDialogUiState.Idle
                    joinCode = code
                    _guideToJoin.value = JoinSharedGuideCardUiState(guide = result.data)
                }

                is Result.Failure ->
                    _joinGuideState.value = JoinSharedGuideDialogUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun onDismissJoinGuideError() {
        _joinGuideState.value = JoinSharedGuideDialogUiState.Idle
    }

    fun onJoinGuideConfirm() {
        val guideToJoin = _guideToJoin.value ?: return
        val code = joinCode ?: return

        viewModelScope.launch {
            _guideToJoin.value = guideToJoin.copy(isJoining = true, joinErrorCode = null)

            when (val result = joinGuide(guideToJoin.guide.id, code)) {
                is Result.Success -> {
                    joinCode = null
                    _guideToJoin.value = null
                    _events.emit(UiEvent.GuideJoined(result.data.id))
                }

                is Result.Failure -> {
                    val current = _guideToJoin.value ?: return@launch
                    _guideToJoin.value = current.copy(isJoining = false, joinErrorCode = result.error.toErrorCode())
                }
            }
        }
    }

    fun onDismissJoinGuideCard() {
        joinCode = null
        _guideToJoin.value = null
    }

    sealed interface UiEvent {
        data class GuideJoined(val guideId: String) : UiEvent
    }
}
