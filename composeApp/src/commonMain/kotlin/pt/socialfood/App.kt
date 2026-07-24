package pt.socialfood

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.data.network.SessionManager
import pt.socialfood.presentation.favourite.FavouritesSyncEffect
import pt.socialfood.presentation.navigation.NavigationRoot
import pt.socialfood.presentation.sign_in.SignInScreen
import pt.socialfood.presentation.sign_up.SignUpScreen
import pt.socialfood.presentation.splash.SplashScreen
import pt.socialfood.presentation.validate_code.ValidateCodeScreen
import pt.socialfood.ui.theme.AppTheme

private sealed class AppDestination {
    data object Splash : AppDestination()
    data object Home : AppDestination()
    data object Login : AppDestination()
    data object SignUp : AppDestination()
    data class ValidateCode(val email: String) : AppDestination()
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
                onNavigateToValidateCode = { email -> navigate(AppDestination.ValidateCode(email)) },
            )
            AppDestination.Home -> {
                NavigationRoot()
                FavouritesSyncEffect()
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
