package pt.socialfood.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.socialfood.presentation.startup.StartupUiState
import pt.socialfood.presentation.startup.StartupViewModel

@Composable
expect fun SplashScreen(
    viewModel: StartupViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
)

@Composable
internal fun SplashNavigationEffect(
    viewModel: StartupViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when (val currentState = state) {
            StartupUiState.NavigateToHome -> onNavigateToHome()
            StartupUiState.NavigateToLogin -> onNavigateToLogin()
            is StartupUiState.NavigateToValidateCode -> onNavigateToValidateCode(currentState.email)
            StartupUiState.Loading -> Unit
        }
    }
}