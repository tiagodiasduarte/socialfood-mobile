package pt.socialfood.presentation.guide.shared.join

import androidx.compose.runtime.Composable

@Composable
actual fun JoinSharedGuideCodeInput(
    show: Boolean,
    state: JoinSharedGuideDialogUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (show) {
        JoinSharedGuideDialog(state = state, onConfirm = onConfirm, onDismiss = onDismiss)
    }
}
