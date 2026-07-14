package pt.socialfood.presentation.guides.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.guide.GetGuideByIdUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

class GuideDetailViewModel(
    private val getGuideById: GetGuideByIdUseCase,
    private val getUserMe: GetUserMeUseCase,
    private val guideId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<GuideDetailUiState>(GuideDetailUiState.Loading)
    val state: StateFlow<GuideDetailUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = GuideDetailUiState.Loading
            val guideDeferred = async { getGuideById(guideId) }
            val userDeferred = async { getUserMe() }
            val guideResult = guideDeferred.await()
            val userResult = userDeferred.await()
            _state.value = when (guideResult) {
                is Result.Success -> GuideDetailUiState.Loaded(
                    guide = guideResult.data,
                    currentUserId = (userResult as? Result.Success)?.data?.id,
                )
                is Result.Error -> GuideDetailUiState.Error
            }
        }
    }
}
