package pt.socialfood.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.domain.use_case.configs.GetConfigsUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

class SplashViewModel(
    private val getUserMe: GetUserMeUseCase,
    private val getConfigs: GetConfigsUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state: StateFlow<SplashUiState> = _state

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            if (settingsRepository.getToken() == null) {
                val pendingEmail = settingsRepository.getPendingVerificationEmail()
                _state.value = if (pendingEmail != null) {
                    SplashUiState.NavigateToValidateToken(pendingEmail)
                } else {
                    SplashUiState.NavigateToLogin
                }
                return@launch
            }

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