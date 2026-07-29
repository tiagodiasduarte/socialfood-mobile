package pt.socialfood.presentation.splash

import androidx.compose.runtime.Composable

@Composable
expect fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
)