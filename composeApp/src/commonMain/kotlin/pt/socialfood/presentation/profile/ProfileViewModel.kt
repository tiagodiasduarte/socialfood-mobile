package pt.socialfood.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.author.GetUserAuthorProfileUseCase
import pt.socialfood.presentation.error.toErrorCode

class ProfileViewModel(private val getUserAuthorProfile: GetUserAuthorProfileUseCase) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            _state.value = when (val result = getUserAuthorProfile()) {
                is Result.Success -> ProfileUiState.Loaded(result.data)
                is Result.Failure -> ProfileUiState.Error(result.error.toErrorCode())
            }
        }
    }
}
