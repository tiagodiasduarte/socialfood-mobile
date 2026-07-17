package pt.socialfood

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.data.network.SessionManager
import pt.socialfood.presentation.navigation.NavigationRoot
import pt.socialfood.presentation.sign_in.SignInScreen
import pt.socialfood.presentation.sign_up.SignUpScreen
import pt.socialfood.presentation.splash.SplashScreen
import pt.socialfood.presentation.validate_token.ValidateTokenScreen
import pt.socialfood.ui.theme.AppTheme

private sealed class AppDestination {
    data object Splash : AppDestination()
    data object Home : AppDestination()
    data object Login : AppDestination()
    data object SignUp : AppDestination()
    data class ValidateToken(val email: String) : AppDestination()
}

@Composable
fun App() {
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
                onNavigateToHome = { navigate(AppDestination.Home) },
                onNavigateToLogin = { navigate(AppDestination.Login) },
                onNavigateToValidateToken = { email -> navigate(AppDestination.ValidateToken(email)) },
            )
            AppDestination.Home -> NavigationRoot()
            AppDestination.Login -> SignInScreen(
                onSignInSuccess = { navigate(AppDestination.Home) },
                onSignUpClick = { navigate(AppDestination.SignUp) },
            )
            AppDestination.SignUp -> SignUpScreen(
                onSignUpSuccess = { email -> navigate(AppDestination.ValidateToken(email)) },
                onSignInClick = { navigate(AppDestination.Login) },
            )
            is AppDestination.ValidateToken -> ValidateTokenScreen(
                email = dest.email,
                onValidateSuccess = { navigate(AppDestination.Home) },
                onBackClick = { navigate(AppDestination.SignUp) },
                onRestartSignUp = { navigate(AppDestination.SignUp) },
            )
        }
    }
}
