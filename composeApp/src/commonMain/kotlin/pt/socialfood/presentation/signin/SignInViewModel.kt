package pt.socialfood.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.usecase.login.LoginUseCase
import pt.socialfood.domain.usecase.login.LoginWithGoogleUseCase
import pt.socialfood.domain.usecase.user.GetUserMeUseCase
import pt.socialfood.presentation.error.toErrorCode
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.sign_in_invalid_email
import socialfood.composeapp.generated.resources.sign_in_invalid_password

class SignInViewModel(
    private val login: LoginUseCase,
    private val loginWithGoogle: LoginWithGoogleUseCase,
    private val getUserMe: GetUserMeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val state: StateFlow<SignInUiState> = _state

    fun onSignIn(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty()) {
                _state.value = SignInUiState.ValidationError(Res.string.sign_in_invalid_email)
                return@launch
            }

            if (password.isEmpty()) {
                _state.value = SignInUiState.ValidationError(Res.string.sign_in_invalid_password)
                return@launch
            }

            _state.value = SignInUiState.Loading

            when (val result = login(email, password)) {
                is Result.Success -> onLoginSucceeded()
                is Result.Failure -> _state.value = SignInUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _state.value = SignInUiState.Loading
            when (val result = loginWithGoogle(idToken)) {
                is Result.Success -> onLoginSucceeded()
                is Result.Failure -> _state.value = SignInUiState.Error(result.error.toErrorCode())
            }
        }
    }

    private suspend fun onLoginSucceeded() {
        _state.value = when (val result = getUserMe()) {
            is Result.Success -> SignInUiState.Success
            is Result.Failure -> SignInUiState.Error(result.error.toErrorCode())
        }
    }

    @Suppress("UnusedParameter")
    fun onGoogleSignInError(message: String) {
        _state.value = SignInUiState.Error(ErrorCode.UNKNOWN)
    }

    fun resetState() {
        _state.value = SignInUiState.Idle
    }
}
