package pt.socialfood.feature.settings.drawer

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLogoutConfirmationLauncher(onConfirm: () -> Unit): () -> Unit
