package pt.socialfood.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
actual fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // MainActivity keeps the native splash on screen (setKeepOnScreenCondition) until
    // this resolves, so there is nothing to render here.
    LaunchedEffect(state) {
        when (val currentState = state) {
            SplashUiState.NavigateToHome -> onNavigateToHome()
            SplashUiState.NavigateToLogin -> onNavigateToLogin()
            is SplashUiState.NavigateToValidateCode -> onNavigateToValidateCode(currentState.email)
            SplashUiState.Loading -> Unit
        }
    }
}