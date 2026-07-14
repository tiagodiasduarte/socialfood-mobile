package pt.socialfood.presentation.validate_token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.use_case.login.ResendVerificationUseCase
import pt.socialfood.domain.use_case.login.ValidateTokenUseCase

class ValidateTokenViewModel(
    private val validateToken: ValidateTokenUseCase,
    private val resendVerification: ResendVerificationUseCase,
    val email: String,
) : ViewModel() {

    private val _state = MutableStateFlow<ValidateTokenUiState>(ValidateTokenUiState.Idle)
    val state: StateFlow<ValidateTokenUiState> = _state

    fun onValidate(token: String) {
        viewModelScope.launch {
            if (token.isEmpty()) {
                _state.value = ValidateTokenUiState.Error(ErrorEntity.InvalidCredentials)
                return@launch
            }

            _state.value = ValidateTokenUiState.Loading

            when (validateToken(token)) {
                is Result.Success -> _state.value = ValidateTokenUiState.Success
                is Result.Error -> _state.value = ValidateTokenUiState.Error(ErrorEntity.Unknown)
            }
        }
    }

    fun onResendCode() {
        viewModelScope.launch {
            resendVerification(email)
        }
    }
}
