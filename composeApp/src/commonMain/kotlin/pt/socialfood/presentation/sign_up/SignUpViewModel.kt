package pt.socialfood.presentation.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.RegisterUseCase
import pt.socialfood.presentation.error.displayMessage
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.sign_up_fill_all_fields
import socialfood.composeapp.generated.resources.sign_up_password_mismatch

class SignUpViewModel(
    private val register: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val state: StateFlow<SignUpUiState> = _state

    fun onSignUp(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                _state.value = SignUpUiState.Error(getString(Res.string.sign_up_fill_all_fields))
                return@launch
            }

            if (password != confirmPassword) {
                _state.value = SignUpUiState.Error(getString(Res.string.sign_up_password_mismatch))
                return@launch
            }

            _state.value = SignUpUiState.Loading

            when (val result = register(name, email, password)) {
                is Result.Success -> _state.value = SignUpUiState.Success(email)
                is Result.Failure -> _state.value = SignUpUiState.Error(result.error.displayMessage())
            }
        }
    }
}
