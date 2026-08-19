package pt.socialfood.presentation.splash

import androidx.compose.runtime.Composable
import pt.socialfood.presentation.startup.StartupViewModel

@Composable
actual fun SplashScreen(
    viewModel: StartupViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
) {
    // MainActivity keeps the native splash on screen (setKeepOnScreenCondition) until
    // this resolves, so there is nothing to render here.
    SplashNavigationEffect(viewModel, onNavigateToHome, onNavigateToLogin, onNavigateToValidateCode)
}
