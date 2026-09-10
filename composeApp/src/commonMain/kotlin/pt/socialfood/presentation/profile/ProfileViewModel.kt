package pt.socialfood.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.author.GetAuthorByIdUseCase
import pt.socialfood.domain.usecase.user.GetUserMeUseCase
import pt.socialfood.presentation.error.toErrorCode

class ProfileViewModel(private val getUserMe: GetUserMeUseCase, private val getAuthorById: GetAuthorByIdUseCase) :
    ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            _state.value = when (val userResult = getUserMe()) {
                is Result.Success -> when (val authorResult = getAuthorById(userResult.data.id)) {
                    is Result.Success -> ProfileUiState.Loaded(authorResult.data)
                    is Result.Failure -> ProfileUiState.Error(authorResult.error.toErrorCode())
                }
                is Result.Failure -> ProfileUiState.Error(userResult.error.toErrorCode())
            }
        }
    }
}
