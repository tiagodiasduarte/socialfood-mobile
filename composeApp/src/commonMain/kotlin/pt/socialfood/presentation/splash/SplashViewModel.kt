package pt.socialfood.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.use_case.configs.GetConfigsUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

class SplashViewModel(
    private val getUserMe: GetUserMeUseCase,
    private val getConfigs: GetConfigsUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state: StateFlow<SplashUiState> = _state

    init {
        load()
    }

    private fun load() {
        if (sessionManager.token == null) {
            _state.value = SplashUiState.NavigateToLogin
            return
        }

        viewModelScope.launch {
            val userDeferred = async { getUserMe() }
            val configsDeferred = async { getConfigs() }

            val userResult = userDeferred.await()
            val configsResult = configsDeferred.await()

            _state.value = if (userResult is Result.Success && configsResult is Result.Success) {
                if (userResult.data.isVerified) {
                    SplashUiState.NavigateToHome
                } else {
                    SplashUiState.NavigateToValidateToken(userResult.data.email)
                }
            } else {
                SplashUiState.NavigateToLogin
            }
        }
    }
}