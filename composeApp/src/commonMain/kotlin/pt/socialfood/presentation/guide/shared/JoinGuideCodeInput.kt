package pt.socialfood.presentation.guide.shared

import androidx.compose.runtime.Composable

@Composable
expect fun JoinGuideCodeInput(
    show: Boolean,
    state: JoinGuideUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
)
