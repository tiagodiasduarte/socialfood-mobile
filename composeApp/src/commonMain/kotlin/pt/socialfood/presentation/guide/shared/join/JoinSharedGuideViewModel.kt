package pt.socialfood.presentation.guide.shared.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.guide.GetGuideByIdUseCase
import pt.socialfood.domain.usecase.guide.JoinGuideUseCase
import pt.socialfood.presentation.error.toErrorCode

class JoinSharedGuideViewModel(
    private val getGuideById: GetGuideByIdUseCase,
    private val joinGuide: JoinGuideUseCase,
    private val guideId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<JoinSharedGuideScreenUiState>(JoinSharedGuideScreenUiState.Loading)
    val state: StateFlow<JoinSharedGuideScreenUiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = JoinSharedGuideScreenUiState.Loading
            _state.value = when (val result = getGuideById(guideId)) {
                is Result.Success -> JoinSharedGuideScreenUiState.Loaded(guide = result.data)
                is Result.Failure -> JoinSharedGuideScreenUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun onJoinClick() {
        val loaded = _state.value as? JoinSharedGuideScreenUiState.Loaded ?: return

        viewModelScope.launch {
            _state.value = loaded.copy(isJoining = true, joinErrorCode = null)

            when (val result = joinGuide(guideId)) {
                is Result.Success -> _events.emit(UiEvent.Joined(result.data.id))
                is Result.Failure -> {
                    val current = _state.value as? JoinSharedGuideScreenUiState.Loaded ?: return@launch
                    _state.value = current.copy(isJoining = false, joinErrorCode = result.error.toErrorCode())
                }
            }
        }
    }

    sealed interface UiEvent {
        data class Joined(val guideId: String) : UiEvent
    }
}
