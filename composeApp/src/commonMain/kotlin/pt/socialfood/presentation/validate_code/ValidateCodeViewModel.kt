package pt.socialfood.presentation.validate_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.use_case.login.ResendVerificationCodeUseCase
import pt.socialfood.domain.use_case.login.RestartSignUpUseCase
import pt.socialfood.domain.use_case.login.ValidateCodeUseCase

class ValidateCodeViewModel(
    private val validateCode: ValidateCodeUseCase,
    private val resendVerificationCode: ResendVerificationCodeUseCase,
    private val restartSignUp: RestartSignUpUseCase,
    val email: String,
) : ViewModel() {

    private val _state = MutableStateFlow<ValidateCodeUiState>(ValidateCodeUiState.Idle)
    val state: StateFlow<ValidateCodeUiState> = _state

    fun onValidate(token: String) {
        viewModelScope.launch {
            if (token.isEmpty()) {
                _state.value = ValidateCodeUiState.Error(ErrorEntity.InvalidCredentials)
                return@launch
            }

            _state.value = ValidateCodeUiState.Loading

            when (validateCode(email = email, token = token)) {
                is Result.Success -> _state.value = ValidateCodeUiState.Success
                is Result.Error -> _state.value = ValidateCodeUiState.Error(ErrorEntity.Unknown)
            }
        }
    }

    fun onResendCode() {
        viewModelScope.launch {
            resendVerificationCode(email)
        }
    }

    fun onRestartSignUp() {
        viewModelScope.launch {
            when (restartSignUp()) {
                is Result.Success -> _state.value = ValidateCodeUiState.RestartSignUp
                is Result.Error -> _state.value = ValidateCodeUiState.RestartSignUp
            }
        }
    }
}
