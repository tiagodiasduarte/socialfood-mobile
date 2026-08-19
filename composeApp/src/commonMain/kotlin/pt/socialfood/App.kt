package pt.socialfood

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.data.network.SessionManager
import pt.socialfood.presentation.navigation.NavigationRoot
import pt.socialfood.presentation.signin.SignInScreen
import pt.socialfood.presentation.signup.SignUpScreen
import pt.socialfood.presentation.splash.SplashScreen
import pt.socialfood.presentation.startup.StartupViewModel
import pt.socialfood.presentation.sync.SyncEffect
import pt.socialfood.presentation.validatecode.ValidateCodeScreen
import pt.socialfood.ui.theme.AppTheme

private sealed class AppDestination {
    data object Splash : AppDestination()
    data object Home : AppDestination()
    data object Login : AppDestination()
    data object SignUp : AppDestination()
    data class ValidateCode(val email: String) : AppDestination()
}

@Composable
fun App(prewarmedStartupViewModel: StartupViewModel? = null) {
    val sessionManager: SessionManager = remember { getKoin().get() }
    val destinationState = remember { mutableStateOf<AppDestination>(AppDestination.Splash) }
    val destination by destinationState
    val navigate: (AppDestination) -> Unit = { destinationState.value = it }

    LaunchedEffect(sessionManager) {
        sessionManager.unauthorizedEvent.collect {
            navigate(AppDestination.Login)
        }
    }

    AppTheme {
        when (val dest = destination) {
            AppDestination.Splash -> SplashScreen(
                viewModel = prewarmedStartupViewModel ?: koinViewModel(),
                onNavigateToHome = { navigate(AppDestination.Home) },
                onNavigateToLogin = { navigate(AppDestination.Login) },
                onNavigateToValidateCode = { email -> navigate(AppDestination.ValidateCode(email)) },
            )
            AppDestination.Home -> {
                NavigationRoot()
                SyncEffect()
            }
            AppDestination.Login -> SignInScreen(
                onSignInSuccess = { navigate(AppDestination.Home) },
                onSignUpClick = { navigate(AppDestination.SignUp) },
            )
            AppDestination.SignUp -> SignUpScreen(
                onSignUpSuccess = { email -> navigate(AppDestination.ValidateCode(email)) },
                onSignInClick = { navigate(AppDestination.Login) },
            )
            is AppDestination.ValidateCode -> ValidateCodeScreen(
                email = dest.email,
                onValidateSuccess = { navigate(AppDestination.Home) },
                onRestartSignUp = { navigate(AppDestination.SignUp) },
            )
        }
    }
}
