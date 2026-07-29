package pt.socialfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.presentation.splash.SplashUiState
import pt.socialfood.presentation.splash.SplashViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashViewModel = getKoin().get<SplashViewModel>()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.state.value == SplashUiState.Loading }

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            App(prewarmedSplashViewModel = splashViewModel)
        }
    }
}