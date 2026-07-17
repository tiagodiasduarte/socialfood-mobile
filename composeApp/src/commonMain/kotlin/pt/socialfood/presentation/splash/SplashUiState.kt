package pt.socialfood.presentation.splash

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NavigateToHome : SplashUiState()
    object NavigateToLogin : SplashUiState()
    data class NavigateToValidateToken(val email: String) : SplashUiState()
}