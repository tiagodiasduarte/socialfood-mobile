package pt.socialfood.presentation.drawer

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.profile_logout_confirmation_cancel
import socialfood.composeapp.generated.resources.profile_logout_confirmation_confirm
import socialfood.composeapp.generated.resources.profile_logout_confirmation_message
import socialfood.composeapp.generated.resources.profile_logout_confirmation_title

@Composable
actual fun rememberLogoutConfirmationLauncher(onConfirm: () -> Unit): () -> Unit {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(Res.string.profile_logout_confirmation_title)) },
            text = { Text(stringResource(Res.string.profile_logout_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onConfirm()
                    },
                ) {
                    Text(stringResource(Res.string.profile_logout_confirmation_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(Res.string.profile_logout_confirmation_cancel))
                }
            },
        )
    }

    return { showDialog = true }
}
