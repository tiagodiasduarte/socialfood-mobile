package pt.socialfood.presentation.validatecode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.login.ResendVerificationCodeUseCase
import pt.socialfood.domain.usecase.login.RestartSignUpUseCase
import pt.socialfood.domain.usecase.login.ValidateCodeUseCase
import pt.socialfood.presentation.error.toErrorCode
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.validate_code_empty_code

class ValidateCodeViewModel(
    private val validateCode: ValidateCodeUseCase,
    private val resendVerificationCode: ResendVerificationCodeUseCase,
    private val restartSignUp: RestartSignUpUseCase,
    val email: String,
) : ViewModel() {

    private val _state = MutableStateFlow<ValidateCodeUiState>(ValidateCodeUiState.Idle)
    val state: StateFlow<ValidateCodeUiState> = _state

    fun onValidate(code: String) {
        viewModelScope.launch {
            if (code.isEmpty()) {
                _state.value = ValidateCodeUiState.ValidationError(Res.string.validate_code_empty_code)
                return@launch
            }

            _state.value = ValidateCodeUiState.Loading

            when (val result = validateCode(email = email, code = code)) {
                is Result.Success -> _state.value = ValidateCodeUiState.Success
                is Result.Failure -> _state.value = ValidateCodeUiState.Error(result.error.toErrorCode())
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
                is Result.Failure -> _state.value = ValidateCodeUiState.RestartSignUp
            }
        }
    }
}
