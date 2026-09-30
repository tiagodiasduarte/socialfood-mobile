package pt.socialfood

import androidx.compose.runtime.Composable

/** Whether the app should render in dark mode, resolved per platform. */
@Composable
internal expect fun rememberUseDarkTheme(): Boolean
