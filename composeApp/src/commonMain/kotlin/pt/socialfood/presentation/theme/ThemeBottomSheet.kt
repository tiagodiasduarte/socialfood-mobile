package pt.socialfood.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.ThemeMode

@Composable
fun ThemeBottomSheet(onDismiss: () -> Unit, viewModel: ThemeViewModel = koinViewModel()) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    ThemeBottomSheetContent(
        selectedThemeMode = themeMode,
        onThemeModeSelected = viewModel::onThemeModeSelected,
        onDismiss = onDismiss,
    )
}

@Composable
internal expect fun ThemeBottomSheetContent(
    selectedThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
)
