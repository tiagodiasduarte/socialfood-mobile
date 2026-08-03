package pt.socialfood.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.LogoutUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase
import pt.socialfood.domain.use_case.user.ObserveUserUseCase

class ProfileViewModel(
    private val getUserMe: GetUserMeUseCase,
    private val logout: LogoutUseCase,
    private val observeUser: ObserveUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state

    init {
        viewModelScope.launch {
            observeUser().filterNotNull().collect { user ->
                _state.value = ProfileUiState.Loaded(user)
            }
        }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = when (val result = getUserMe()) {
                is Result.Success -> ProfileUiState.Loaded(result.data)
                is Result.Failure -> ProfileUiState.Error
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.value = ProfileUiState.Loading
            logout.invoke()
            _state.value = ProfileUiState.LoggedOut
        }
    }
}
