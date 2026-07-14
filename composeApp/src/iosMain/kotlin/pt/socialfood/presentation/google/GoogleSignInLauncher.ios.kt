package pt.socialfood.presentation.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

interface GoogleSignInDelegate {
    fun signIn(onIdToken: (String) -> Unit, onError: (String) -> Unit)
}

object GoogleSignInBridge {
    var delegate: GoogleSignInDelegate? = null
}

@Composable
actual fun rememberGoogleSignInLauncher(
    onIdToken: (String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit = remember {
    {
        val delegate = GoogleSignInBridge.delegate
        if (delegate != null) {
            delegate.signIn(onIdToken, onError)
        } else {
            onError("GoogleSignInBridge delegate not set")
        }
    }
}
