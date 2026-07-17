package pt.socialfood.presentation.splash

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NavigateToHome : SplashUiState()
    object NavigateToLogin : SplashUiState()
    data class NavigateToValidateCode(val email: String) : SplashUiState()
}