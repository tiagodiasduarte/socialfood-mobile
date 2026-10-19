package pt.socialfood.core.designsystem.component

import androidx.compose.runtime.Composable

@Composable
expect fun ErrorAlertDialog(title: String, message: String, confirmButtonText: String, onDismiss: () -> Unit)
