package pt.socialfood.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun ErrorAlertDialog(title: String, message: String, confirmButtonText: String, onDismiss: () -> Unit)
