package pt.socialfood.presentation.imagepicker

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(
    onResult: (bytes: ByteArray, mimeType: String) -> Unit,
): () -> Unit
