package pt.socialfood.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.domain.use_case.configs.GetConfigsUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

private const val MIN_STARTUP_DURATION_MILLIS = 1000L

class StartupViewModel(
    private val getUserMe: GetUserMeUseCase,
    private val getConfigs: GetConfigsUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<StartupUiState>(StartupUiState.Loading)
    val state: StateFlow<StartupUiState> = _state

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val minDuration = async { delay(MIN_STARTUP_DURATION_MILLIS) }

            val resultState = if (settingsRepository.getToken() == null) {
                val pendingEmail = settingsRepository.getPendingVerificationEmail()
                if (pendingEmail != null) {
                    StartupUiState.NavigateToValidateCode(pendingEmail)
                } else {
                    StartupUiState.NavigateToLogin
                }
            } else {
                val userDeferred = async { getUserMe() }
                val configsDeferred = async { getConfigs() }

                val userResult = userDeferred.await()
                val configsResult = configsDeferred.await()

                if (userResult is Result.Success && configsResult is Result.Success) {
                    if (userResult.data.isVerified) {
                        StartupUiState.NavigateToHome
                    } else {
                        StartupUiState.NavigateToValidateCode(userResult.data.email)
                    }
                } else {
                    StartupUiState.NavigateToLogin
                }
            }

            minDuration.await()
            _state.value = resultState
        }
    }
}