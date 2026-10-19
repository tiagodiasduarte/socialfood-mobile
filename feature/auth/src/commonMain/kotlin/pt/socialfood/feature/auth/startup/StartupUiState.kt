package pt.socialfood.feature.auth.startup

sealed class StartupUiState {
    object Loading : StartupUiState()
    object NavigateToHome : StartupUiState()
    object NavigateToLogin : StartupUiState()
    data class NavigateToValidateCode(val email: String) : StartupUiState()
}
