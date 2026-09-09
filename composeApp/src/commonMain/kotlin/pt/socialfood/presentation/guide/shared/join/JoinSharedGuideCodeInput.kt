package pt.socialfood.presentation.guide.shared.join

import androidx.compose.runtime.Composable

@Composable
expect fun JoinSharedGuideCodeInput(
    show: Boolean,
    state: JoinSharedGuideDialogUiState,
    onConfirm: (code: String) -> Unit,
    onDismiss: () -> Unit,
)
