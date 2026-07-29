package pt.socialfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.presentation.splash.SplashUiState
import pt.socialfood.presentation.splash.StartupViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val startupViewModel = getKoin().get<StartupViewModel>()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { startupViewModel.state.value == SplashUiState.Loading }

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            App(prewarmedStartupViewModel = startupViewModel)
        }
    }
}