package pt.socialfood.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.social_food_splash

private val SplashBackgroundColor = Color(0xFFF54900)

@Composable
actual fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when (val currentState = state) {
            SplashUiState.NavigateToHome -> onNavigateToHome()
            SplashUiState.NavigateToLogin -> onNavigateToLogin()
            is SplashUiState.NavigateToValidateCode -> onNavigateToValidateCode(currentState.email)
            SplashUiState.Loading -> Unit
        }
    }

    // iOS has no equivalent to Android's setKeepOnScreenCondition, so this holds the
    // branded screen visible while `viewModel` resolves. No animation: it's a static
    // continuation of the same background/icon iOS's native launch screen just showed.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.social_food_splash),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
        )
    }
}