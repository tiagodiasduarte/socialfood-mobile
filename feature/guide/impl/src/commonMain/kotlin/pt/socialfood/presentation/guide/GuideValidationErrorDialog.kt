package pt.socialfood.presentation.guide

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource

@Composable
expect fun GuideValidationErrorDialog(errors: List<StringResource>, onDismiss: () -> Unit)
