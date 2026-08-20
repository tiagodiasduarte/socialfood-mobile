package pt.socialfood.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pt.socialfood.presentation.startup.StartupViewModel
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.social_food_splash

@Composable
actual fun SplashScreen(
    viewModel: StartupViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToValidateCode: (email: String) -> Unit,
) {
    SplashNavigationEffect(viewModel, onNavigateToHome, onNavigateToLogin, onNavigateToValidateCode)

    // iOS has no equivalent to Android's setKeepOnScreenCondition, so this holds the
    // branded screen visible while `viewModel` resolves. No animation: it's a static
    // continuation of the same background/icon iOS's native launch screen just showed.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.social_food_splash),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
        )
    }
}
