package pt.socialfood.presentation.guide.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.guide.GetGuideByIdUseCase
import pt.socialfood.presentation.error.toErrorCode

class GuideMapViewModel(private val getGuideById: GetGuideByIdUseCase, private val guideId: String) : ViewModel() {

    private val _state = MutableStateFlow<GuideMapUiState>(GuideMapUiState.Loading)
    val state: StateFlow<GuideMapUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = GuideMapUiState.Loading
            _state.value = when (val result = getGuideById(guideId)) {
                is Result.Success -> GuideMapUiState.Loaded(guide = result.data)
                is Result.Failure -> GuideMapUiState.Error(result.error.toErrorCode())
            }
        }
    }
}
