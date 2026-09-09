package pt.socialfood.presentation.guide.shared

import androidx.compose.runtime.Composable

@Composable
actual fun JoinGuideCodeInput(
    show: Boolean,
    state: JoinGuideUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (show) {
        JoinGuideDialog(state = state, onConfirm = onConfirm, onDismiss = onDismiss)
    }
}
