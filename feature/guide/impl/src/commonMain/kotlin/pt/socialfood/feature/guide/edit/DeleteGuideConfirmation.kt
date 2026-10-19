package pt.socialfood.feature.guide.edit

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDeleteGuideConfirmationLauncher(onConfirm: () -> Unit): () -> Unit
