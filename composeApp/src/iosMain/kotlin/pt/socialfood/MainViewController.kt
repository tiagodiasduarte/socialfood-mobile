package pt.socialfood

import androidx.compose.ui.window.ComposeUIViewController

// PascalCase because Swift calls it as `MainViewControllerKt.MainViewController()`.
@Suppress("FunctionNaming", "ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController { App() }
