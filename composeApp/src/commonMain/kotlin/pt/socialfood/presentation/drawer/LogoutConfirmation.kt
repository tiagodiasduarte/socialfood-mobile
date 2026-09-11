package pt.socialfood.presentation.drawer

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLogoutConfirmationLauncher(onConfirm: () -> Unit): () -> Unit
