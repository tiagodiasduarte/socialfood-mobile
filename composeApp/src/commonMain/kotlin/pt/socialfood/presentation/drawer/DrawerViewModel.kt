package pt.socialfood.presentation.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.login.LogoutUseCase
import pt.socialfood.domain.usecase.user.GetUserMeUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.presentation.error.toErrorCode

class DrawerViewModel(
    private val getUserMe: GetUserMeUseCase,
    private val logout: LogoutUseCase,
    private val observeUser: ObserveUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<DrawerUiState>(DrawerUiState.Loading)
    val state: StateFlow<DrawerUiState> = _state

    init {
        viewModelScope.launch {
            observeUser().filterNotNull().collect { user ->
                _state.value = DrawerUiState.Loaded(user)
            }
        }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = when (val result = getUserMe()) {
                is Result.Success -> DrawerUiState.Loaded(result.data)
                is Result.Failure -> DrawerUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.value = DrawerUiState.Loading
            logout.invoke()
            _state.value = DrawerUiState.LoggedOut
        }
    }
}
