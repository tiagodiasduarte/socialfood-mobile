package pt.socialfood.presentation.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.use_case.login.RegisterUseCase

class SignUpViewModel(
    private val register: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val state: StateFlow<SignUpUiState> = _state

    fun onSignUp(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                _state.value = SignUpUiState.Error(ErrorEntity.InvalidCredentials)
                return@launch
            }

            if (password != confirmPassword) {
                _state.value = SignUpUiState.Error(ErrorEntity.PasswordMismatch)
                return@launch
            }

            _state.value = SignUpUiState.Loading

            when (register(name, email, password)) {
                is Result.Success -> _state.value = SignUpUiState.Success(email)
                is Result.Failure -> _state.value = SignUpUiState.Error(ErrorEntity.Unknown)
            }
        }
    }
}
