package pt.socialfood.presentation.guide.edit

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDeleteGuideConfirmationLauncher(onConfirm: () -> Unit): () -> Unit
