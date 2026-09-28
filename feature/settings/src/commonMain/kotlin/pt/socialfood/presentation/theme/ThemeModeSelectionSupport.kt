package pt.socialfood.presentation.theme

/**
 * Whether the app should offer a Light/Dark/System picker at all. iOS always follows the
 * system appearance, so there's nothing to pick there -- see [pt.socialfood.ui.theme.resolveUseDarkTheme].
 */
expect val isThemeModeSelectionSupported: Boolean
