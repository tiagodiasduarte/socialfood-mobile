package pt.socialfood.presentation.guide.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.guide.IsGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.guide.GetGuideByIdUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase
import pt.socialfood.presentation.error.toErrorCode

class GuideDetailViewModel(
    private val getGuideById: GetGuideByIdUseCase,
    private val getUserMe: GetUserMeUseCase,
    private val isGuideFavourite: IsGuideFavouriteUseCase,
    private val markGuideFavourite: MarkGuideFavouriteUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
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
            val isFavouriteDeferred = async { isGuideFavourite(guideId) }
            val guideResult = guideDeferred.await()
            val userResult = userDeferred.await()
            val isFavouriteResult = isFavouriteDeferred.await()
            _state.value = when (guideResult) {
                is Result.Success -> GuideDetailUiState.Loaded(
                    guide = guideResult.data,
                    currentUserId = (userResult as? Result.Success)?.data?.id,
                    isFavourite = (isFavouriteResult as? Result.Success)?.data ?: false,
                )
                is Result.Failure -> GuideDetailUiState.Error(guideResult.error.toErrorCode())
            }
        }
    }

    fun toggleFavourite() {
        val current = _state.value as? GuideDetailUiState.Loaded ?: return
        val newIsFavourite = !current.isFavourite
        _state.value = current.copy(isFavourite = newIsFavourite)

        viewModelScope.launch {
            val result = if (newIsFavourite) {
                markGuideFavourite(current.guide)
            } else {
                unmarkGuideFavourite(current.guide.id)
            }
            if (result is Result.Failure) {
                val stateNow = _state.value as? GuideDetailUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(isFavourite = !newIsFavourite)
            }
        }
    }
}
