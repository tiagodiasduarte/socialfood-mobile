package pt.socialfood.presentation.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import pt.socialfood.BuildConfig

@Composable
actual fun rememberGoogleSignInLauncher(
    onIdToken: (String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    return remember(context) {
        {
            scope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)

                    val googleIdOption =
                        GetGoogleIdOption
                            .Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                            .build()

                    val request =
                        GetCredentialRequest
                            .Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                    val result =
                        credentialManager.getCredential(
                            context = context,
                            request = request,
                        )

                    val credential = result.credential
                    if (
                        credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        try {
                            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            onIdToken(googleCredential.idToken)
                        } catch (e: GoogleIdTokenParsingException) {
                            onError("Token parsing failed: ${e.message}")
                        }
                    } else {
                        val msg = "Unexpected credential type: ${credential.type}"
                        onError(msg)
                    }
                } catch (e: GetCredentialException) {
                    onError("${e::class.simpleName}: ${e.message}")
                } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                    // Deliberately broad: a last-resort safety net for anything unexpected from the
                    // Google Identity SDK (e.g. config/runtime errors), not just credential failures.
                    // Uncaught here would propagate out of this coroutine instead of calling onError().
                    onError("${e::class.simpleName}: ${e.message}")
                }
            }
        }
    }
}
