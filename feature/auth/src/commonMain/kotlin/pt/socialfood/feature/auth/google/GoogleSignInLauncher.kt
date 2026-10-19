package pt.socialfood.feature.auth.google

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleSignInLauncher(onIdToken: (String) -> Unit, onError: (String) -> Unit): () -> Unit
