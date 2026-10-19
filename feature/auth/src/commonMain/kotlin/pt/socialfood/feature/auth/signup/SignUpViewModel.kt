package pt.socialfood.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.usecase.login.RegisterUseCase
import pt.socialfood.core.ui.error.toErrorCode
import pt.socialfood.feature.auth.generated.resources.Res
import pt.socialfood.feature.auth.generated.resources.sign_up_fill_all_fields
import pt.socialfood.feature.auth.generated.resources.sign_up_password_mismatch

class SignUpViewModel(private val register: RegisterUseCase) : ViewModel() {

    private val _state = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val state: StateFlow<SignUpUiState> = _state

    fun onSignUp(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            val hasEmptyField = name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            if (hasEmptyField) {
                _state.value = SignUpUiState.ValidationError(Res.string.sign_up_fill_all_fields)
                return@launch
            }

            if (password != confirmPassword) {
                _state.value = SignUpUiState.ValidationError(Res.string.sign_up_password_mismatch)
                return@launch
            }

            _state.value = SignUpUiState.Loading

            when (val result = register(name, email, password)) {
                is Result.Success -> _state.value = SignUpUiState.Success(email)
                is Result.Failure -> _state.value = SignUpUiState.Error(result.error.toErrorCode())
            }
        }
    }
}
